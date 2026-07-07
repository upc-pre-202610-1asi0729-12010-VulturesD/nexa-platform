package com.nexa.platform.tenantmanagement.application.internal;

import com.nexa.platform.shared.domain.exceptions.BusinessRuleException;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.tenantmanagement.application.commands.TenantManagementCommands.*;
import com.nexa.platform.tenantmanagement.application.commandservices.TenantManagementCommandService;
import com.nexa.platform.tenantmanagement.application.dtos.TenantManagementResponses.*;
import com.nexa.platform.tenantmanagement.application.queryservices.TenantManagementQueryService;
import com.nexa.platform.tenantmanagement.domain.model.*;
import com.nexa.platform.tenantmanagement.domain.model.repositories.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantManagementService implements TenantManagementCommandService, TenantManagementQueryService {
    private final TenantRepositoryPort tenants;
    private final OrganizationRegistrationRequestRepositoryPort registrations;
    private final TenantMemberRepositoryPort members;
    private final TenantRuleRepositoryPort rules;
    private final TenantCustomFieldRepositoryPort customFields;
    private final TenantSubscriptionRepositoryPort subscriptions;
    private final WorkspaceRepositoryPort workspaces;
    private final WorkspaceFeatureRepositoryPort workspaceFeatures;
    private final UserWorkspaceMembershipRepositoryPort memberships;
    private final WorkspacePreferenceRepositoryPort preferences;
    private final TenantManagementMapper mapper;

    public TenantManagementService(TenantRepositoryPort tenants, OrganizationRegistrationRequestRepositoryPort registrations,
                                   TenantMemberRepositoryPort members, TenantRuleRepositoryPort rules,
                                   TenantCustomFieldRepositoryPort customFields, TenantSubscriptionRepositoryPort subscriptions,
                                   WorkspaceRepositoryPort workspaces, WorkspaceFeatureRepositoryPort workspaceFeatures,
                                   UserWorkspaceMembershipRepositoryPort memberships, WorkspacePreferenceRepositoryPort preferences,
                                   TenantManagementMapper mapper) {
        this.tenants = tenants;
        this.registrations = registrations;
        this.members = members;
        this.rules = rules;
        this.customFields = customFields;
        this.subscriptions = subscriptions;
        this.workspaces = workspaces;
        this.workspaceFeatures = workspaceFeatures;
        this.memberships = memberships;
        this.preferences = preferences;
        this.mapper = mapper;
    }

    @Override
    public List<TenantResponse> listTenants() {
        return tenants.findAllByOrderByIdAsc().stream().map(mapper::toTenantResponse).toList();
    }

    @Override
    public TenantResponse getTenant(Long id) {
        return mapper.toTenantResponse(findTenant(id));
    }

    @Override
    public TenantResponse getTenantBySlug(String slug) {
        return mapper.toTenantResponse(workspaces.findBySlug(slug)
            .flatMap(workspace -> tenants.findById(workspace.getTenantId()))
            .or(() -> tenants.findBySlug(slug))
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", slug)));
    }

    @Override
    public List<OrganizationRegistrationResponse> listOrganizationRegistrations() {
        return registrations.findAllByOrderByIdDesc().stream().map(mapper::toOrganizationRegistrationResponse).toList();
    }

    @Override
    public OrganizationRegistrationResponse getOrganizationRegistration(String externalId) {
        return mapper.toOrganizationRegistrationResponse(registrations.findByExternalId(externalId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization registration", externalId)));
    }

    @Override
    public List<TenantMemberResponse> listTenantMembers(Long tenantId) {
        ensureTenantExists(tenantId);
        return members.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toTenantMemberResponse).toList();
    }

    @Override
    public TenantMemberResponse getTenantMember(Long id) {
        return mapper.toTenantMemberResponse(findTenantMember(id));
    }

    @Override
    public List<TenantRuleResponse> listTenantRules(Long tenantId) {
        ensureTenantExists(tenantId);
        return rules.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toTenantRuleResponse).toList();
    }

    @Override
    public TenantRuleResponse getTenantRule(Long id) {
        return mapper.toTenantRuleResponse(findTenantRule(id));
    }

    @Override
    public List<TenantCustomFieldResponse> listTenantCustomFields(Long tenantId) {
        ensureTenantExists(tenantId);
        return customFields.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toTenantCustomFieldResponse).toList();
    }

    @Override
    public TenantCustomFieldResponse getTenantCustomField(Long id) {
        return mapper.toTenantCustomFieldResponse(findTenantCustomField(id));
    }

    @Override
    public List<TenantSubscriptionResponse> listTenantSubscriptions(Long tenantId) {
        ensureTenantExists(tenantId);
        return subscriptions.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toTenantSubscriptionResponse).toList();
    }

    @Override
    public TenantSubscriptionResponse getTenantSubscription(Long id) {
        return mapper.toTenantSubscriptionResponse(findTenantSubscription(id));
    }

    @Override
    public List<WorkspaceResponse> listWorkspaces(Long tenantId) {
        if (tenantId == null) return workspaces.findAllByOrderByIdAsc().stream().map(mapper::toWorkspaceResponse).toList();
        ensureTenantExists(tenantId);
        return workspaces.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toWorkspaceResponse).toList();
    }

    @Override
    public WorkspaceResponse getWorkspace(Long id) {
        return mapper.toWorkspaceResponse(findWorkspace(id));
    }

    @Override
    public WorkspaceResponse getWorkspaceBySlug(String slug) {
        return mapper.toWorkspaceResponse(workspaces.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace", slug)));
    }

    @Override
    public List<WorkspaceFeatureResponse> listWorkspaceFeatures(Long workspaceId) {
        ensureWorkspaceExists(workspaceId);
        return workspaceFeatures.findByWorkspaceIdOrderByIdAsc(workspaceId).stream().map(mapper::toWorkspaceFeatureResponse).toList();
    }

    @Override
    public WorkspaceFeatureResponse getWorkspaceFeature(Long id) {
        return mapper.toWorkspaceFeatureResponse(findWorkspaceFeature(id));
    }

    @Override
    public List<UserWorkspaceMembershipResponse> listUserWorkspaceMemberships(Long tenantId, Long workspaceId) {
        if (workspaceId != null) {
            ensureWorkspaceExists(workspaceId);
            return memberships.findByWorkspaceIdOrderByIdAsc(workspaceId).stream().map(mapper::toUserWorkspaceMembershipResponse).toList();
        }
        ensureTenantExists(tenantId);
        return memberships.findByTenantIdOrderByIdAsc(tenantId).stream().map(mapper::toUserWorkspaceMembershipResponse).toList();
    }

    @Override
    public UserWorkspaceMembershipResponse getUserWorkspaceMembership(Long id) {
        return mapper.toUserWorkspaceMembershipResponse(findUserWorkspaceMembership(id));
    }

    @Override
    public List<WorkspacePreferenceResponse> listWorkspacePreferences(Long workspaceId) {
        ensureWorkspaceExists(workspaceId);
        return preferences.findByWorkspaceIdOrderByIdAsc(workspaceId).stream().map(mapper::toWorkspacePreferenceResponse).toList();
    }

    @Override
    public WorkspacePreferenceResponse getWorkspacePreference(Long id) {
        return mapper.toWorkspacePreferenceResponse(findWorkspacePreference(id));
    }

    @Override
    @Transactional
    public OrganizationRegistrationResponse handle(CreateOrganizationRegistrationCommand command) {
        if (command.externalId() != null && registrations.existsByExternalId(command.externalId())) {
            throw new BusinessRuleException("Organization registration external id already exists");
        }
        var request = new OrganizationRegistrationRequest(command.externalId(), command.status(), command.companyName(),
            command.workspaceName(), command.workspaceSlug(), command.adminEmail(), command.payloadJson());
        return mapper.toOrganizationRegistrationResponse(registrations.save(request));
    }

    @Override
    @Transactional
    public TenantResponse handle(UpsertTenantCommand command) {
        Tenant tenant = command.id() == null ? null : findTenant(command.id());
        if (tenant == null) {
            if (tenants.existsBySlug(command.slug())) throw new BusinessRuleException("Tenant slug already exists");
            if (tenants.existsByRuc(command.ruc())) throw new BusinessRuleException("Tenant RUC already exists");
            tenant = new Tenant(command.name(), command.legalName(), command.slug(), command.ruc(), command.workspaceUrl(),
                command.emailDomain(), command.plan(), command.status(), command.country());
        } else {
            tenant.update(command.name(), command.legalName(), command.slug(), command.ruc(), command.workspaceUrl(),
                command.emailDomain(), command.plan(), command.status(), command.country());
        }
        return mapper.toTenantResponse(tenants.save(tenant));
    }

    @Override
    @Transactional
    public TenantMemberResponse handle(UpsertTenantMemberCommand command) {
        ensureTenantExists(command.tenantId());
        TenantMember member = command.id() == null ? new TenantMember(command.tenantId(), command.email(), command.fullName(),
            command.role(), command.department(), command.status()) : members.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Tenant member", command.id()));
        if (command.id() != null) member.update(command.tenantId(), command.email(), command.fullName(), command.role(), command.department(), command.status());
        return mapper.toTenantMemberResponse(members.save(member));
    }

    @Override
    @Transactional
    public TenantRuleResponse handle(UpsertTenantRuleCommand command) {
        ensureTenantExists(command.tenantId());
        TenantRule rule = command.id() == null ? new TenantRule(command.tenantId(), command.code(), command.name(), command.description(), command.category(), command.enabled())
            : rules.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Tenant rule", command.id()));
        if (command.id() != null) rule.update(command.tenantId(), command.code(), command.name(), command.description(), command.category(), command.enabled());
        return mapper.toTenantRuleResponse(rules.save(rule));
    }

    @Override
    @Transactional
    public TenantCustomFieldResponse handle(UpsertTenantCustomFieldCommand command) {
        ensureTenantExists(command.tenantId());
        TenantCustomField field = command.id() == null ? new TenantCustomField(command.tenantId(), command.code(), command.label(), command.targetResource(), command.fieldType(), command.required(), command.enabled())
            : customFields.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Tenant custom field", command.id()));
        if (command.id() != null) field.update(command.tenantId(), command.code(), command.label(), command.targetResource(), command.fieldType(), command.required(), command.enabled());
        return mapper.toTenantCustomFieldResponse(customFields.save(field));
    }

    @Override
    @Transactional
    public TenantSubscriptionResponse handle(UpsertTenantSubscriptionCommand command) {
        ensureTenantExists(command.tenantId());
        TenantSubscription subscription = command.id() == null ? new TenantSubscription(command.tenantId(), command.plan(), command.seats(), command.warehouses(), command.paymentStatus(), command.nextBillingDate(), command.billingContact())
            : subscriptions.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Tenant subscription", command.id()));
        if (command.id() != null) subscription.update(command.tenantId(), command.plan(), command.seats(), command.warehouses(), command.paymentStatus(), command.nextBillingDate(), command.billingContact());
        return mapper.toTenantSubscriptionResponse(subscriptions.save(subscription));
    }

    @Override
    @Transactional
    public WorkspaceResponse handle(UpsertWorkspaceCommand command) {
        ensureTenantExists(command.tenantId());
        Workspace workspace = command.id() == null ? null : findWorkspace(command.id());
        if (workspace == null) {
            if (workspaces.existsBySlug(command.slug())) throw new BusinessRuleException("Workspace slug already exists");
            workspace = new Workspace(command.tenantId(), command.name(), command.slug(), command.url(), command.emailDomain(), command.status(), command.primaryWorkspace());
        } else {
            workspace.update(command.tenantId(), command.name(), command.slug(), command.url(), command.emailDomain(), command.status(), command.primaryWorkspace());
        }
        return mapper.toWorkspaceResponse(workspaces.save(workspace));
    }

    @Override
    @Transactional
    public WorkspaceFeatureResponse handle(UpsertWorkspaceFeatureCommand command) {
        ensureWorkspaceExists(command.workspaceId());
        WorkspaceFeature feature = command.id() == null ? new WorkspaceFeature(command.workspaceId(), command.code(), command.name(), command.enabled())
            : workspaceFeatures.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Workspace feature", command.id()));
        if (command.id() != null) feature.update(command.workspaceId(), command.code(), command.name(), command.enabled());
        return mapper.toWorkspaceFeatureResponse(workspaceFeatures.save(feature));
    }

    @Override
    @Transactional
    public UserWorkspaceMembershipResponse handle(UpsertUserWorkspaceMembershipCommand command) {
        ensureTenantExists(command.tenantId());
        ensureWorkspaceExists(command.workspaceId());
        UserWorkspaceMembership membership = command.id() == null ? new UserWorkspaceMembership(command.tenantId(), command.workspaceId(),
            command.userId(), command.email(), command.fullName(), command.role(), command.department(), command.status(),
            command.portalAccess(), command.clientAccountId())
            : memberships.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("User workspace membership", command.id()));
        if (command.id() != null) membership.update(command.tenantId(), command.workspaceId(), command.userId(), command.email(), command.fullName(),
            command.role(), command.department(), command.status(), command.portalAccess(), command.clientAccountId());
        return mapper.toUserWorkspaceMembershipResponse(memberships.save(membership));
    }

    @Override
    @Transactional
    public WorkspacePreferenceResponse handle(UpsertWorkspacePreferenceCommand command) {
        ensureTenantExists(command.tenantId());
        ensureWorkspaceExists(command.workspaceId());
        WorkspacePreference preference = command.id() == null ? new WorkspacePreference(command.tenantId(), command.workspaceId(), command.key(), command.value(), command.valueType())
            : preferences.findById(command.id()).orElseThrow(() -> new ResourceNotFoundException("Workspace preference", command.id()));
        if (command.id() != null) preference.update(command.tenantId(), command.workspaceId(), command.key(), command.value(), command.valueType());
        return mapper.toWorkspacePreferenceResponse(preferences.save(preference));
    }

    @Override
    @Transactional
    public void deleteTenantMember(Long id) {
        members.delete(findTenantMember(id));
    }

    @Override
    @Transactional
    public void deleteTenantRule(Long id) {
        rules.delete(findTenantRule(id));
    }

    @Override
    @Transactional
    public void deleteTenantCustomField(Long id) {
        customFields.delete(findTenantCustomField(id));
    }

    @Override
    @Transactional
    public void deleteTenantSubscription(Long id) {
        subscriptions.delete(findTenantSubscription(id));
    }

    @Override
    @Transactional
    public void deleteWorkspace(Long id) {
        workspaces.delete(findWorkspace(id));
    }

    @Override
    @Transactional
    public void deleteWorkspaceFeature(Long id) {
        workspaceFeatures.delete(findWorkspaceFeature(id));
    }

    @Override
    @Transactional
    public void deleteUserWorkspaceMembership(Long id) {
        memberships.delete(findUserWorkspaceMembership(id));
    }

    @Override
    @Transactional
    public void deleteWorkspacePreference(Long id) {
        preferences.delete(findWorkspacePreference(id));
    }

    private Tenant findTenant(Long id) {
        return tenants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
    }

    private Workspace findWorkspace(Long id) {
        return workspaces.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workspace", id));
    }

    private TenantMember findTenantMember(Long id) {
        return members.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant member", id));
    }

    private TenantRule findTenantRule(Long id) {
        return rules.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant rule", id));
    }

    private TenantCustomField findTenantCustomField(Long id) {
        return customFields.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant custom field", id));
    }

    private TenantSubscription findTenantSubscription(Long id) {
        return subscriptions.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant subscription", id));
    }

    private WorkspaceFeature findWorkspaceFeature(Long id) {
        return workspaceFeatures.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workspace feature", id));
    }

    private UserWorkspaceMembership findUserWorkspaceMembership(Long id) {
        return memberships.findById(id).orElseThrow(() -> new ResourceNotFoundException("User workspace membership", id));
    }

    private WorkspacePreference findWorkspacePreference(Long id) {
        return preferences.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workspace preference", id));
    }

    private void ensureTenantExists(Long id) {
        if (id == null) throw new BusinessRuleException("Tenant id is required");
        findTenant(id);
    }

    private void ensureWorkspaceExists(Long id) {
        if (id == null) throw new BusinessRuleException("Workspace id is required");
        findWorkspace(id);
    }
}
