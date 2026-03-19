package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.PasswordRulesResponse;
import com.um.springbootprojstructure.dto.PasswordRulesUpdateRequest;
import com.um.springbootprojstructure.service.PasswordRulesService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminPasswordRulesController {

    private final PasswordRulesService passwordRulesService;

    public AdminPasswordRulesController(PasswordRulesService passwordRulesService) {
        this.passwordRulesService = passwordRulesService;
    }

    @GetMapping("/password-rules")
    @PreAuthorize("hasRole('ADMIN')")
    public PasswordRulesResponse getRules() {
        return passwordRulesService.getActiveRules();
    }

    @PutMapping("/password-rules")
    @PreAuthorize("hasRole('ADMIN')")
    public PasswordRulesResponse updateRules(Authentication authentication,
                                             @Valid @RequestBody PasswordRulesUpdateRequest req) {
        return passwordRulesService.updateRules(req, authentication == null ? "admin" : authentication.getName());
    }
}
