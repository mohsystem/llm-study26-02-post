package com.um.springbootprojstructure.dto;

import java.time.Instant;

public class PublicUserProfileResponse {

    private String publicRef;
    private Instant createdAt;

    public String getPublicRef() { return publicRef; }
    public Instant getCreatedAt() { return createdAt; }

    public void setPublicRef(String publicRef) { this.publicRef = publicRef; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
