package com.bridgelabz.fundoonotes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class AccountToken {
    public enum Purpose { VERIFY, RESET }

    @Id
    @Column(name = "token_value")
    private String value;
    @Enumerated(EnumType.STRING)
    private Purpose purpose;
    private Instant expiresAt;
    @ManyToOne(optional = false)
    private AppUser user;

    protected AccountToken() { }

    public AccountToken(String value, Purpose purpose, Instant expiresAt, AppUser user) {
        this.value = value;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public String getValue() { return value; }
    public Purpose getPurpose() { return purpose; }
    public Instant getExpiresAt() { return expiresAt; }
    public AppUser getUser() { return user; }
}
