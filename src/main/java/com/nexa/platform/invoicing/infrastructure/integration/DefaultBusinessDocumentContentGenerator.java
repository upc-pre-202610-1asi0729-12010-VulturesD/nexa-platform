package com.nexa.platform.invoicing.infrastructure.integration;

import com.nexa.platform.invoicing.application.outbound.BusinessDocumentContentGenerator;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.SalesOrderItem;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.springframework.stereotype.Component;

@Component
public class DefaultBusinessDocumentContentGenerator implements BusinessDocumentContentGenerator {
    private static final Set<String> SUPPORTED_TYPES = Set.of("factura_xml", "factura_pdf", "guia_pdf");

    @Override
    public GeneratedBusinessDocumentContent generate(SalesOrder order, String type) {
        String normalizedType = normalize(type);
        String orderNumber = "ORD-2026-" + String.format("%04d", order.getId());
        String extension = "factura_xml".equals(normalizedType) ? "xml" : "pdf";
        String fileName = orderNumber.toLowerCase() + "-" + normalizedType + "-" +
            OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "." + extension;
        String label = switch (normalizedType) {
            case "factura_xml" -> "Factura XML";
            case "factura_pdf" -> "Factura PDF";
            default -> "Guia de remision PDF";
        };
        byte[] content = "factura_xml".equals(normalizedType)
            ? buildInvoiceXml(orderNumber, order)
            : buildPdf("guia_pdf".equals(normalizedType) ? "GUIA DE REMISION" : "FACTURA", orderNumber, order);
        return new GeneratedBusinessDocumentContent(content, fileName,
            "xml".equals(extension) ? "application/xml; charset=utf-8" : "application/pdf",
            label, order.getCustomer().getId());
    }

    private static String normalize(String type) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Document type is required.");
        String normalized = type.trim().toLowerCase();
        if (!SUPPORTED_TYPES.contains(normalized)) throw new IllegalArgumentException("Document type cannot be generated.");
        return normalized;
    }

    private static byte[] buildInvoiceXml(String orderNumber, SalesOrder order) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            var writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output, StandardCharsets.UTF_8.name());
            writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
            writer.writeStartElement("Invoice");
            writer.writeAttribute("version", "1.0");
            element(writer, "DocumentNumber", orderNumber);
            element(writer, "IssueDate", OffsetDateTime.now().toLocalDate().toString());
            element(writer, "Currency", "PEN");
            writer.writeStartElement("Customer");
            element(writer, "BusinessName", order.getCustomer().getBusinessName());
            element(writer, "Ruc", order.getCustomer().getTaxId());
            element(writer, "Email", order.getCustomer().getContactEmail());
            writer.writeEndElement();
            writer.writeStartElement("Delivery");
            element(writer, "Address", order.getCustomer().getDeliveryAddress());
            writer.writeEndElement();
            writer.writeStartElement("Lines");
            int lineNumber = 1;
            for (SalesOrderItem item : order.getItems()) {
                writer.writeStartElement("Line");
                writer.writeAttribute("number", String.valueOf(lineNumber++));
                element(writer, "Description", item.getProduct().getName());
                element(writer, "Quantity", String.valueOf(item.getQuantity()));
                element(writer, "UnitPrice", item.getUnitPrice().toPlainString());
                element(writer, "Subtotal", item.subtotal().toPlainString());
                writer.writeEndElement();
            }
            writer.writeEndElement();
            element(writer, "Total", order.total().toPlainString());
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            return output.toByteArray();
        } catch (XMLStreamException exception) {
            throw new IllegalStateException("Invoice XML generation failed.", exception);
        }
    }

    private static void element(javax.xml.stream.XMLStreamWriter writer, String name, String value)
        throws XMLStreamException {
        writer.writeStartElement(name);
        writer.writeCharacters(value == null ? "" : value);
        writer.writeEndElement();
    }

    private static byte[] buildPdf(String title, String orderNumber, SalesOrder order) {
        List<String> lines = new ArrayList<>();
        lines.add(title);
        lines.add("Nexa");
        lines.add("Pedido: " + orderNumber);
        lines.add("Fecha: " + OffsetDateTime.now().toLocalDate());
        lines.add("Cliente: " + order.getCustomer().getBusinessName());
        lines.add("RUC cliente: " + order.getCustomer().getTaxId());
        lines.add("Entrega: " + order.getCustomer().getDeliveryAddress());
        lines.add("");
        lines.add("DETALLE");
        for (SalesOrderItem item : order.getItems()) {
            lines.add(item.getQuantity() + " x " + item.getProduct().getName() + " | PEN " +
                item.getUnitPrice().toPlainString() + " | " + item.subtotal().toPlainString());
        }
        lines.add("");
        lines.add("TOTAL: PEN " + order.total().toPlainString());
        return simplePdf(lines);
    }

    private static byte[] simplePdf(List<String> lines) {
        StringBuilder stream = new StringBuilder("BT\n/F1 11 Tf\n50 750 Td\n14 TL\n");
        lines.stream().limit(44).forEach(line ->
            stream.append('(').append(escapePdfText(line)).append(") Tj\nT*\n"));
        stream.append("ET\n");
        byte[] streamBytes = stream.toString().getBytes(StandardCharsets.US_ASCII);
        String[] objects = {
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
            "<< /Length " + streamBytes.length + " >>\nstream\n" + stream + "endstream"
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        writeAscii(output, "%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int index = 0; index < objects.length; index++) {
            offsets.add(output.size());
            writeAscii(output, (index + 1) + " 0 obj\n" + objects[index] + "\nendobj\n");
        }
        int xrefOffset = output.size();
        writeAscii(output, "xref\n0 " + (objects.length + 1) + "\n0000000000 65535 f \n");
        offsets.stream().skip(1).forEach(offset -> writeAscii(output, String.format("%010d 00000 n \n", offset)));
        writeAscii(output, "trailer\n<< /Size " + (objects.length + 1) +
            " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n");
        return output.toByteArray();
    }

    private static String escapePdfText(String value) {
        String ascii = value == null ? "" : value.replaceAll("[^\\x20-\\x7E]", "");
        return ascii.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void writeAscii(ByteArrayOutputStream output, String value) {
        output.writeBytes(value.getBytes(StandardCharsets.US_ASCII));
    }
}
