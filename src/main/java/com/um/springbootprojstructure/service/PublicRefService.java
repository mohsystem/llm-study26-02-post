package com.um.springbootprojstructure.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PublicRefService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a URL-safe, non-guessable public reference.
     * 18 bytes -> 24 chars (approx), 144 bits of entropy.
     */
    public String newPublicRef() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
