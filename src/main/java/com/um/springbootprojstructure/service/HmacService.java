package com.um.springbootprojstructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class HmacService {

    private final byte[] keyBytes;

    public HmacService(@Value("${security.password-reset.hmac-secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("HMAC secret must be set via env and be at least 32 characters.");
        }
        this.keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String hmacSha256Base64Url(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
            byte[] out = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("crypto_error");
        }
    }
}
