package com.um.springbootprojstructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBootstrapService {

    private final boolean enabled;
    private final String setupTokenHash;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapService(
            @Value("${security.bootstrap-admin.enabled:false}") boolean enabled,
            @Value("${security.bootstrap-admin.setup-token-hash:}") String setupTokenHash,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.enabled = enabled;
        this.setupTokenHash = setupTokenHash == null ? "" : setupTokenHash.trim();
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void bootstrapAdmin(String providedToken, String email, String password) {
        if (!enabled) {
            // deny by default
            throw new IllegalArgumentException("admin_setup_not_allowed");
        }
        if (setupTokenHash.isEmpty()) {
            // misconfiguration: do not allow setup without configured token hash
            throw new IllegalStateException("admin_setup_misconfigured");
        }
        if (providedToken == null || providedToken.isBlank()) {
            throw new IllegalArgumentException("admin_setup_not_allowed");
        }

        // One-time semantics: if admin exists, no more bootstrap
        if (userService.anyAdminExists()) {
            throw new IllegalArgumentException("admin_setup_not_allowed");
        }

        // Verify provided token against stored hash (BCrypt)
        boolean ok = passwordEncoder.matches(providedToken, setupTokenHash);
        if (!ok) {
            // deterministic response
            throw new IllegalArgumentException("admin_setup_not_allowed");
        }

        userService.createAdmin(email, password);

        // Note: This service does not mutate config.
        // For true one-time use, keep enabled=false after use (CI/test pipeline),
        // or rotate the token hash.
    }
}
