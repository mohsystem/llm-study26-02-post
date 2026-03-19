package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserProfileRequest {

    @Size(min = 1, max = 80)
    // allow letters, digits, spaces and a small safe set of punctuation
    @Pattern(regexp = "^[\\p{L}\\p{N} .,'-]{1,80}$", message = "invalid")
    private String displayName;

    @Size(max = 500)
    private String bio;

    @Size(max = 300)
    // Keep URL validation conservative to reduce injection/scheme abuse.
    // You can tighten further as needed.
    @Pattern(regexp = "^(https?://).*$", message = "invalid")
    private String avatarUrl;

    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
