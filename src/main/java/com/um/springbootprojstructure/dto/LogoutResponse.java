package com.um.springbootprojstructure.dto;

public class LogoutResponse {
    private final boolean success;

    public LogoutResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
