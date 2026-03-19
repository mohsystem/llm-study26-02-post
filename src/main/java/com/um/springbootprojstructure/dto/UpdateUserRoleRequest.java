package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleRequest {

    @NotNull
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
