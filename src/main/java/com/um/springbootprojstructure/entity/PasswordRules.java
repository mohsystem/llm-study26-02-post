package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "password_rules")
public class PasswordRules {

    @Id
    private Long id = 1L; // single-row table pattern

    @Column(nullable = false)
    private int minLength = 12;

    @Column(nullable = false)
    private int maxLength = 72; // align with BCrypt effective limit

    @Column(nullable = false)
    private boolean requireUppercase = true;

    @Column(nullable = false)
    private boolean requireLowercase = true;

    @Column(nullable = false)
    private boolean requireDigit = true;

    @Column(nullable = false)
    private boolean requireSpecial = true;

    @Column(nullable = false)
    private int minSpecial = 1;

    @Column(nullable = false)
    private int minDigits = 1;

    @Column(nullable = false)
    private int minUppercase = 1;

    @Column(nullable = false)
    private int minLowercase = 1;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(nullable = false, length = 254)
    private String updatedBy = "system";

    public PasswordRules() {}

    public Long getId() { return id; }

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

    public void setId(Long id) { this.id = id; }

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
