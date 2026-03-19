package com.um.springbootprojstructure.dto;

public class DeterministicDecisionResponse {
    private final boolean accepted;

    public DeterministicDecisionResponse(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
