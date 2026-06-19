package com.nexa.platform.iam.interfaces.rest.transform;

import com.nexa.platform.iam.application.dtos.AuthResponse;
import com.nexa.platform.iam.application.dtos.LoginRequest;
import com.nexa.platform.iam.application.dtos.RegisterRequest;
import com.nexa.platform.iam.interfaces.rest.resources.AuthResource;
import com.nexa.platform.iam.interfaces.rest.resources.LoginResource;
import com.nexa.platform.iam.interfaces.rest.resources.RegisterResource;

public final class AuthResourceAssembler {
    private AuthResourceAssembler() { }

    public static RegisterRequest toRequestFromResource(RegisterResource resource) {
        return new RegisterRequest(resource.fullName(), resource.email(), resource.password());
    }

    public static LoginRequest toRequestFromResource(LoginResource resource) {
        return new LoginRequest(resource.email(), resource.password());
    }

    public static AuthResource toResourceFromEntity(AuthResponse response) {
        return new AuthResource(response.token(), response.tokenType(),
            UserResourceFromEntityAssembler.toResourceFromEntity(response.user()));
    }
}
