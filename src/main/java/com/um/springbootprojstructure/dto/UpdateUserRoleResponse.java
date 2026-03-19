package com.um.springbootprojstructure.dto;

public class UpdateUserRoleResponse {

    private Long userId;
    private String role;

    public Long getUserId() { return userId; }
    public String getRole() { return role; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(String role) { this.role = role; }
}
