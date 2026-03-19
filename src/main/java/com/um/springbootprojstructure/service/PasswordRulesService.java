package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.PasswordRulesResponse;
import com.um.springbootprojstructure.dto.PasswordRulesUpdateRequest;
import com.um.springbootprojstructure.entity.PasswordRules;
import com.um.springbootprojstructure.repository.PasswordRulesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordRulesService {

    private static final long SINGLETON_ID = 1L;

    private final PasswordRulesRepository repo;

    public PasswordRulesService(PasswordRulesRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public PasswordRules getActiveRulesEntity() {
        return repo.findById(SINGLETON_ID).orElseGet(() -> {
            // Do not auto-create here (read-only). Caller can create in write txn.
            PasswordRules defaults = new PasswordRules();
            defaults.setId(SINGLETON_ID);
            return defaults;
        });
    }

    @Transactional
    public PasswordRulesResponse getActiveRules() {
        PasswordRules r = repo.findById(SINGLETON_ID).orElseGet(() -> {
            PasswordRules defaults = new PasswordRules();
            defaults.setId(SINGLETON_ID);
            defaults.setUpdatedAt(Instant.now());
            defaults.setUpdatedBy("system");
            return repo.save(defaults);
        });
        return toResponse(r);
    }

    @Transactional
    public PasswordRulesResponse updateRules(PasswordRulesUpdateRequest req, String updatedByEmail) {
        PasswordRules r = repo.findById(SINGLETON_ID).orElseGet(() -> {
            PasswordRules defaults = new PasswordRules();
            defaults.setId(SINGLETON_ID);
            return defaults;
        });

        // Cross-field validation (fail closed)
        if (req.getMinLength() > req.getMaxLength()) {
            throw new IllegalArgumentException("invalid_password_rules");
        }

        // If a category isn't required, its min must be 0.
        enforceMinIfRequired(req.getRequireUppercase(), req.getMinUppercase(), "uppercase");
        enforceMinIfRequired(req.getRequireLowercase(), req.getMinLowercase(), "lowercase");
        enforceMinIfRequired(req.getRequireDigit(), req.getMinDigits(), "digit");
        enforceMinIfRequired(req.getRequireSpecial(), req.getMinSpecial(), "special");

        // Ensure required mins do not exceed maxLength
        int sumMins = req.getMinUppercase() + req.getMinLowercase() + req.getMinDigits() + req.getMinSpecial();
        if (sumMins > req.getMaxLength()) {
            throw new IllegalArgumentException("invalid_password_rules");
        }

        r.setMinLength(req.getMinLength());
        r.setMaxLength(req.getMaxLength());
        r.setRequireUppercase(req.getRequireUppercase());
        r.setRequireLowercase(req.getRequireLowercase());
        r.setRequireDigit(req.getRequireDigit());
        r.setRequireSpecial(req.getRequireSpecial());
        r.setMinUppercase(req.getMinUppercase());
        r.setMinLowercase(req.getMinLowercase());
        r.setMinDigits(req.getMinDigits());
        r.setMinSpecial(req.getMinSpecial());
        r.setUpdatedAt(Instant.now());
        r.setUpdatedBy(updatedByEmail == null ? "admin" : updatedByEmail);

        PasswordRules saved = repo.save(r);
        return toResponse(saved);
    }

    public void validatePasswordOrThrow(String rawPassword, PasswordRules rules) {
        if (rawPassword == null) throw new IllegalArgumentException("password_policy_violation");

        int len = rawPassword.length();
        if (len < rules.getMinLength() || len > rules.getMaxLength()) {
            throw new IllegalArgumentException("password_policy_violation");
        }

        int upper = 0, lower = 0, digit = 0, special = 0;
        for (int i = 0; i < rawPassword.length(); i++) {
            char c = rawPassword.charAt(i);
            if (Character.isUpperCase(c)) upper++;
            else if (Character.isLowerCase(c)) lower++;
            else if (Character.isDigit(c)) digit++;
            else special++;
        }

        if (rules.isRequireUppercase() && upper < rules.getMinUppercase()) throw new IllegalArgumentException("password_policy_violation");
        if (rules.isRequireLowercase() && lower < rules.getMinLowercase()) throw new IllegalArgumentException("password_policy_violation");
        if (rules.isRequireDigit() && digit < rules.getMinDigits()) throw new IllegalArgumentException("password_policy_violation");
        if (rules.isRequireSpecial() && special < rules.getMinSpecial()) throw new IllegalArgumentException("password_policy_violation");
    }

    private void enforceMinIfRequired(Boolean required, Integer min, String name) {
        if (required == null || min == null) throw new IllegalArgumentException("invalid_password_rules");
        if (!required && min != 0) throw new IllegalArgumentException("invalid_password_rules");
    }

    private PasswordRulesResponse toResponse(PasswordRules r) {
        PasswordRulesResponse resp = new PasswordRulesResponse();
        resp.setMinLength(r.getMinLength());
        resp.setMaxLength(r.getMaxLength());
        resp.setRequireUppercase(r.isRequireUppercase());
        resp.setRequireLowercase(r.isRequireLowercase());
        resp.setRequireDigit(r.isRequireDigit());
        resp.setRequireSpecial(r.isRequireSpecial());
        resp.setMinUppercase(r.getMinUppercase());
        resp.setMinLowercase(r.getMinLowercase());
        resp.setMinDigits(r.getMinDigits());
        resp.setMinSpecial(r.getMinSpecial());
        resp.setUpdatedAt(r.getUpdatedAt());
        resp.setUpdatedBy(r.getUpdatedBy());
        return resp;
    }
}
