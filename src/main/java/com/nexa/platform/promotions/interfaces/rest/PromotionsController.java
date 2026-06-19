package com.nexa.platform.promotions.interfaces.rest;

import com.nexa.platform.promotions.application.queries.GetAllPromotionsQuery;
import com.nexa.platform.promotions.application.queryservices.PromotionQueryService;
import com.nexa.platform.promotions.interfaces.rest.resources.PromotionResponse;
import com.nexa.platform.promotions.interfaces.rest.transform.PromotionResponseFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for promotion queries.
 *
 * <p>Delegates all queries to {@link PromotionQueryService} and
 * assembles the HTTP response via {@link PromotionResponseFromEntityAssembler}.
 * Follows the interfaces-layer pattern from the course catch-up-platform reference.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/promotions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Promotions", description = "Commercial promotion campaign endpoints")
public class PromotionsController {

    private final PromotionQueryService service;

    public PromotionsController(PromotionQueryService service) {
        this.service = service;
    }

    @Operation(
        summary = "List all promotions",
        description = "Returns all promotion campaigns stored in the promotions bounded context"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion list returned")
    })
    @GetMapping
    public List<PromotionResponse> list() {
        return service.handle(new GetAllPromotionsQuery()).stream()
            .map(PromotionResponseFromEntityAssembler::toResourceFromEntity)
            .toList();
    }
}
