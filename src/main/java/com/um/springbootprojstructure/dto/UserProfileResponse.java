package com.um.springbootprojstructure.dto;

import java.time.Instant;

public class UserProfileResponse {

    private String publicRef;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private Instant createdAt;

    public String getPublicRef() { return publicRef; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public Instant getCreatedAt() { return createdAt; }

    public void setPublicRef(String publicRef) { this.publicRef = publicRef; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
