package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetConfirmRequest {

    @NotBlank
    @Size(min = 20, max = 256)
    private String token;

    @NotBlank
    @Size(min = 1, max = 72)
    private String newPassword;

    public String getToken() { return token; }
    public String getNewPassword() { return newPassword; }

    public void setToken(String token) { this.token = token; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
