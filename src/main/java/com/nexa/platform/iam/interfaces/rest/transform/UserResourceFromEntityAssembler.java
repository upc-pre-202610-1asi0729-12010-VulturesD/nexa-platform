package com.nexa.platform.iam.interfaces.rest.transform;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.interfaces.rest.resources.UserResource;

public final class UserResourceFromEntityAssembler {
    private UserResourceFromEntityAssembler() { }

    public static UserResource toResourceFromEntity(UserResponse response) {
        return new UserResource(response.id(), response.fullName(), response.displayName(), response.email(),
            response.roles(), response.profile(), response.scope(), response.segment(), response.workspace(), response.clientId());
    }
}
