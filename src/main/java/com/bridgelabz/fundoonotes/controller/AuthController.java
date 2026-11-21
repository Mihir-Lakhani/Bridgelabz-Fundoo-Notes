package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.service.AuthService;
import com.bridgelabz.fundoonotes.dto.LoginRequest;
import com.bridgelabz.fundoonotes.dto.RegisterRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", service.register(request)));
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        return Map.of("token", service.login(request));
    }
}
