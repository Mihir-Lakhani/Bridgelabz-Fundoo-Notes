package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.dto.RegisterRequest;
import com.bridgelabz.fundoonotes.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", service.register(request)));
    }
}
