package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.dto.ProfileResponse;
import com.bridgelabz.fundoonotes.dto.UpdateProfileRequest;
import com.bridgelabz.fundoonotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.profile(jwt.getSubject());
    }

    @PutMapping("/me")
    public ProfileResponse update(@Valid @RequestBody UpdateProfileRequest request,
                                  @AuthenticationPrincipal Jwt jwt) {
        return service.updateName(jwt.getSubject(), request.name());
    }
}
