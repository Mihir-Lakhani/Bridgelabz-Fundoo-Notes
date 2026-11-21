package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.AccountToken;
import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.repository.AccountTokenRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountTokenRepository tokens;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final MailService mail;

    public AccountService(AccountTokenRepository tokens, UserRepository users,
                          PasswordEncoder passwordEncoder, MailService mail) {
        this.tokens = tokens;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.mail = mail;
    }

    public void sendVerification(AppUser user) {
        String token = issue(user, AccountToken.Purpose.VERIFY);
        mail.send(user.getEmail(), "Verify your email", "Verification token: " + token);
    }

    @Transactional
    public void verify(String value) {
        AccountToken token = validToken(value, AccountToken.Purpose.VERIFY);
        AppUser user = token.getUser();
        user.setVerified(true);
        users.save(user);
        tokens.delete(token);
    }

    public void forgotPassword(String email) {
        users.findByEmail(email).ifPresent(user -> {
            String token = issue(user, AccountToken.Purpose.RESET);
            mail.send(email, "Reset your password", "Password reset token: " + token);
        });
    }

    @Transactional
    public void resetPassword(String value, String newPassword) {
        AccountToken token = validToken(value, AccountToken.Purpose.RESET);
        AppUser user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        users.save(user);
        tokens.delete(token);
    }

    private String issue(AppUser user, AccountToken.Purpose purpose) {
        String value = UUID.randomUUID().toString();
        tokens.save(new AccountToken(value, purpose, Instant.now().plus(15, ChronoUnit.MINUTES), user));
        return value;
    }

    private AccountToken validToken(String value, AccountToken.Purpose purpose) {
        AccountToken token = tokens.findById(value)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (token.getPurpose() != purpose || !token.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return token;
    }
}
