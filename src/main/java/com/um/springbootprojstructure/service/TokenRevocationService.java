package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.RevokedToken;
import com.um.springbootprojstructure.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenRevocationService {

    private final RevokedTokenRepository revokedTokens;

    public TokenRevocationService(RevokedTokenRepository revokedTokens) {
        this.revokedTokens = revokedTokens;
    }

    @Transactional(readOnly = true)
    public boolean isRevoked(String jti) {
        if (jti == null || jti.isBlank()) return true; // fail closed
        return revokedTokens.existsByJti(jti);
    }

    @Transactional
    public void revoke(String jti, Instant expiresAt) {
        if (jti == null || jti.isBlank() || expiresAt == null) {
            throw new IllegalArgumentException("bad_request");
        }
        if (expiresAt.isBefore(Instant.now())) {
            // Already expired: no need to store
            return;
        }
        // Unique constraint protects against duplicates
        if (!revokedTokens.existsByJti(jti)) {
            revokedTokens.save(new RevokedToken(jti, expiresAt));
        }
    }

    @Transactional
    public long purgeExpired() {
        return revokedTokens.deleteByExpiresAtBefore(Instant.now());
    }
}
