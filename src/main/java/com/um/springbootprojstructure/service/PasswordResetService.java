package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.PasswordResetToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.PasswordResetTokenRepository;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRulesService passwordRulesService;
    private final HmacService hmacService;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();

    private final long ttlSeconds;
    private final int tokenBytes;

    public PasswordResetService(
            UserRepository users,
            PasswordResetTokenRepository tokens,
            PasswordEncoder passwordEncoder,
            PasswordRulesService passwordRulesService,
            HmacService hmacService,
            @Value("${security.password-reset.ttl-seconds}") long ttlSeconds,
            @Value("${security.password-reset.token-bytes}") int tokenBytes
    ) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.passwordRulesService = passwordRulesService;
        this.hmacService = hmacService;
        this.ttlSeconds = Math.min(Math.max(ttlSeconds, 300), 86400); // 5 min .. 24h
        this.tokenBytes = Math.min(Math.max(tokenBytes, 16), 64);     // 128-bit .. 512-bit
    }

    /**
     * Deterministic anti-enumeration: always "accepts" the request externally.
     * Does nothing for unknown/disabled emails.
     *
     * This implementation does not send email. Integrate an EmailService to deliver the token.
     */
    @Transactional
    public void requestReset(String email) {
        if (email == null || email.isBlank()) return;

        User u = users.findByEmailIgnoreCase(email).orElse(null);
        if (u == null || !u.isEnabled()) {
            return;
        }

        String token = generateToken();
        String tokenHash = hmacService.hmacSha256Base64Url(token);
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        tokens.save(new PasswordResetToken(u.getId(), tokenHash, expiresAt));

        // Do NOT log or return the token. Send out-of-band (email/SMS) in real system.
        // For migration/testing, you can temporarily implement a secure admin-only retrieval endpoint,
        // but do not ship it to production.
    }

    /**
     * Deterministic boolean response: true if reset completed, else false.
     */
    @Transactional
    public boolean confirmReset(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null) return false;

        // Apply active password rules
        try {
            passwordRulesService.validatePasswordOrThrow(newPassword, passwordRulesService.getActiveRulesEntity());
        } catch (IllegalArgumentException ex) {
            return false;
        }

        String tokenHash = hmacService.hmacSha256Base64Url(token);
        var opt = tokens.findFirstByTokenHashAndUsedFalse(tokenHash);
        if (opt.isEmpty()) return false;

        PasswordResetToken prt = opt.get();
        Instant now = Instant.now();
        if (prt.isUsed() || prt.getExpiresAt().isBefore(now)) {
            return false;
        }

        User u = users.findById(prt.getUserId()).orElse(null);
        if (u == null || !u.isEnabled()) {
            return false;
        }

        // Mark token used (one-time use), then update password
        prt.setUsed(true);
        tokens.save(prt);

        u.setPasswordHash(passwordEncoder.encode(newPassword));
        users.save(u);

        return true;
    }

    private String generateToken() {
        byte[] b = new byte[tokenBytes];
        secureRandom.nextBytes(b);
        return urlEncoder.encodeToString(b);
    }
}
