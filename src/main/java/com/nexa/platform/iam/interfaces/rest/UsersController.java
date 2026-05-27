package com.nexa.platform.iam.interfaces.rest;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.application.internal.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final CurrentUserService service;

    public UsersController(CurrentUserService service) { this.service = service; }

    @GetMapping("/me")
    public UserResponse me() { return service.currentUser(); }
}
