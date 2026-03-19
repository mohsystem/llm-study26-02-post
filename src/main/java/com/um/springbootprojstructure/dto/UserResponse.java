package com.um.springbootprojstructure.dto;

import com.um.springbootprojstructure.entity.Role;

import java.time.Instant;

public class UserResponse {
    private Long id;
    private String publicRef;
    private String email;
    private Role role;
    private boolean enabled;
    private Instant createdAt;

    public Long getId() { return id; }
    public String getPublicRef() { return publicRef; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setPublicRef(String publicRef) { this.publicRef = publicRef; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(Role role) { this.role = role; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
