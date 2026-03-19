package com.um.springbootprojstructure.dto;

import java.time.Instant;

public class PasswordRulesResponse {

    private int minLength;
    private int maxLength;

    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecial;

    private int minSpecial;
    private int minDigits;
    private int minUppercase;
    private int minLowercase;

    private Instant updatedAt;
    private String updatedBy;

    public int getMinLength() { return minLength; }
    public int getMaxLength() { return maxLength; }

    public boolean isRequireUppercase() { return requireUppercase; }
    public boolean isRequireLowercase() { return requireLowercase; }
    public boolean isRequireDigit() { return requireDigit; }
    public boolean isRequireSpecial() { return requireSpecial; }

    public int getMinSpecial() { return minSpecial; }
    public int getMinDigits() { return minDigits; }
    public int getMinUppercase() { return minUppercase; }
    public int getMinLowercase() { return minLowercase; }

    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }

    public void setMinLength(int minLength) { this.minLength = minLength; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }

    public void setRequireUppercase(boolean requireUppercase) { this.requireUppercase = requireUppercase; }
    public void setRequireLowercase(boolean requireLowercase) { this.requireLowercase = requireLowercase; }
    public void setRequireDigit(boolean requireDigit) { this.requireDigit = requireDigit; }
    public void setRequireSpecial(boolean requireSpecial) { this.requireSpecial = requireSpecial; }

    public void setMinSpecial(int minSpecial) { this.minSpecial = minSpecial; }
    public void setMinDigits(int minDigits) { this.minDigits = minDigits; }
    public void setMinUppercase(int minUppercase) { this.minUppercase = minUppercase; }
    public void setMinLowercase(int minLowercase) { this.minLowercase = minLowercase; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
