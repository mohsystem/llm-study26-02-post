package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PasswordRulesUpdateRequest {

    @NotNull @Min(8) @Max(128)
    private Integer minLength;

    @NotNull @Min(10) @Max(72)
    private Integer maxLength;

    @NotNull
    private Boolean requireUppercase;

    @NotNull
    private Boolean requireLowercase;

    @NotNull
    private Boolean requireDigit;

    @NotNull
    private Boolean requireSpecial;

    @NotNull @Min(0) @Max(10)
    private Integer minSpecial;

    @NotNull @Min(0) @Max(10)
    private Integer minDigits;

    @NotNull @Min(0) @Max(10)
    private Integer minUppercase;

    @NotNull @Min(0) @Max(10)
    private Integer minLowercase;

    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }
    public Boolean getRequireUppercase() { return requireUppercase; }
    public Boolean getRequireLowercase() { return requireLowercase; }
    public Boolean getRequireDigit() { return requireDigit; }
    public Boolean getRequireSpecial() { return requireSpecial; }
    public Integer getMinSpecial() { return minSpecial; }
    public Integer getMinDigits() { return minDigits; }
    public Integer getMinUppercase() { return minUppercase; }
    public Integer getMinLowercase() { return minLowercase; }

    public void setMinLength(Integer minLength) { this.minLength = minLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
    public void setRequireUppercase(Boolean requireUppercase) { this.requireUppercase = requireUppercase; }
    public void setRequireLowercase(Boolean requireLowercase) { this.requireLowercase = requireLowercase; }
    public void setRequireDigit(Boolean requireDigit) { this.requireDigit = requireDigit; }
    public void setRequireSpecial(Boolean requireSpecial) { this.requireSpecial = requireSpecial; }
    public void setMinSpecial(Integer minSpecial) { this.minSpecial = minSpecial; }
    public void setMinDigits(Integer minDigits) { this.minDigits = minDigits; }
    public void setMinUppercase(Integer minUppercase) { this.minUppercase = minUppercase; }
    public void setMinLowercase(Integer minLowercase) { this.minLowercase = minLowercase; }
}
