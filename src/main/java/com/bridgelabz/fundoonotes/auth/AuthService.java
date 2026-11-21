package com.bridgelabz.fundoonotes.auth;

import com.bridgelabz.fundoonotes.user.AppUser;
import com.bridgelabz.fundoonotes.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserRepository users, PasswordEncoder passwords,
                       AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.users = users;
        this.passwords = passwords;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public Long register(RegisterRequest request) {
        if (users.existsByEmail(request.email())) throw new IllegalArgumentException("Email already registered");
        AppUser user = new AppUser(request.name(), request.email(), passwords.encode(request.password()));
        return users.save(user).getId();
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(request.email()).issuedAt(now).expiresAt(now.plus(2, ChronoUnit.HOURS)).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
