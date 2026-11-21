package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.RegisterRequest;
import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public Long register(RegisterRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        AppUser user = new AppUser(request.name(), request.email(),
                passwordEncoder.encode(request.password()));
        return users.save(user).getId();
    }
}
