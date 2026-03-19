package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminUserMergeRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9_-]{10,64}$", message = "invalid")
    private String sourcePublicRef;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9_-]{10,64}$", message = "invalid")
    private String targetPublicRef;

    public String getSourcePublicRef() {
        return sourcePublicRef;
    }

    public void setSourcePublicRef(String sourcePublicRef) {
        this.sourcePublicRef = sourcePublicRef;
    }

    public String getTargetPublicRef() {
        return targetPublicRef;
    }

    public void setTargetPublicRef(String targetPublicRef) {
        this.targetPublicRef = targetPublicRef;
    }
}
