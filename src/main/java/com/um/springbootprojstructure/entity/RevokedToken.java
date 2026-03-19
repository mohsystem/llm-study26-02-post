package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "revoked_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_revoked_tokens_jti", columnNames = "jti")
        },
        indexes = {
                @Index(name = "ix_revoked_tokens_expires_at", columnList = "expiresAt")
        }
)
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant revokedAt = Instant.now();

    protected RevokedToken() {}

    public RevokedToken(String jti, Instant expiresAt) {
        this.jti = jti;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getJti() { return jti; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }

    public void setJti(String jti) { this.jti = jti; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
