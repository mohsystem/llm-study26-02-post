package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByJti(String jti);
    long deleteByExpiresAtBefore(Instant cutoff);
}
