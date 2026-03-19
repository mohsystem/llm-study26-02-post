package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRulesService passwordRulesService;

    public AccountService(UserRepository users, PasswordEncoder passwordEncoder, PasswordRulesService passwordRulesService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.passwordRulesService = passwordRulesService;
    }

    /**
     * Deterministic outcome should be handled at controller level.
     */
    @Transactional
    public boolean changePassword(String authenticatedEmail, String currentPassword, String newPassword) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) return false;

        User u = users.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        if (u == null || !u.isEnabled()) return false;

        boolean currentOk = passwordEncoder.matches(currentPassword, u.getPasswordHash());
        if (!currentOk) return false;

        try {
            passwordRulesService.validatePasswordOrThrow(newPassword, passwordRulesService.getActiveRulesEntity());
        } catch (IllegalArgumentException ex) {
            return false;
        }

        u.setPasswordHash(passwordEncoder.encode(newPassword));
        users.save(u);
        return true;
    }
}
