package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_public_ref", columnNames = "publicRef")
        },
        indexes = {
                @Index(name = "ix_users_public_ref", columnList = "publicRef")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Non-guessable public account reference used for public profile lookup.
     * Generated server-side.
     */
    @Column(nullable = false, length = 64)
    private String publicRef;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 120)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    // ---- Profile fields (allowed to update) ----

    @Column(nullable = false, length = 80)
    private String displayName = "User";

    @Column(nullable = true, length = 500)
    private String bio;

    @Column(nullable = true, length = 300)
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected User() {}

    public User(String publicRef, String email, String passwordHash, Role role) {
        this.publicRef = publicRef;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getPublicRef() { return publicRef; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }

    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }

    public Instant getCreatedAt() { return createdAt; }

    public void setPublicRef(String publicRef) { this.publicRef = publicRef; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
