package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findFirstByTokenHashAndUsedFalse(String tokenHash);
    long deleteByExpiresAtBefore(Instant cutoff);
}
