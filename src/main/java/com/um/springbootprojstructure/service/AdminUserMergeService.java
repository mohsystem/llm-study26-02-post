package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.AdminUserMergeResultResponse;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminUserMergeService {

    private final UserRepository users;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();

    public AdminUserMergeService(UserRepository users) {
        this.users = users;
    }

    /**
     * Merge source into target. Conservative merge rules:
     * - Prefer target's existing non-null/non-blank values.
     * - If target is missing a field and source has it, copy it.
     * - If either is ADMIN, target becomes ADMIN (least surprise for access).
     * - Source account is disabled and tombstoned to prevent future login use.
     */
    @Transactional
    public AdminUserMergeResultResponse mergeByPublicRefs(String sourceRef, String targetRef) {
        if (sourceRef == null || targetRef == null || sourceRef.isBlank() || targetRef.isBlank()) {
            throw new IllegalArgumentException("bad_request");
        }
        if (sourceRef.equals(targetRef)) {
            throw new IllegalArgumentException("merge_same_account");
        }

        User source = users.findByPublicRef(sourceRef).orElseThrow(() -> new IllegalArgumentException("not_found"));
        User target = users.findByPublicRef(targetRef).orElseThrow(() -> new IllegalArgumentException("not_found"));

        if (!source.isEnabled()) {
            throw new IllegalArgumentException("merge_source_disabled");
        }
        if (!target.isEnabled()) {
            throw new IllegalArgumentException("merge_target_disabled");
        }

        Map<String, String> changes = new LinkedHashMap<>();

        // Merge profile fields
        if (isBlank(target.getDisplayName()) && !isBlank(source.getDisplayName())) {
            target.setDisplayName(source.getDisplayName());
            changes.put("displayName", "source->target");
        }
        if (isBlank(target.getBio()) && !isBlank(source.getBio())) {
            target.setBio(source.getBio());
            changes.put("bio", "source->target");
        }
        if (isBlank(target.getAvatarUrl()) && !isBlank(source.getAvatarUrl())) {
            target.setAvatarUrl(source.getAvatarUrl());
            changes.put("avatarUrl", "source->target");
        }

        // Role consolidation: if either is admin, keep admin on target
        if (source.getRole() == Role.ROLE_ADMIN && target.getRole() != Role.ROLE_ADMIN) {
            target.setRole(Role.ROLE_ADMIN);
            changes.put("role", "elevated_to_admin_due_to_source");
        }

        // Persist updated target
        users.save(target);

        // Disable + tombstone source (prevent login and avoid email uniqueness collision)
        source.setEnabled(false);

        // Tombstone email while keeping format roughly valid, avoiding leaking original.
        // Ensure it is unique and non-routable. Keep within 254 chars.
        source.setEmail(tombstoneEmail(source.getId(), source.getPublicRef()));
        // Optional: remove profile fields from source to reduce duplication footprint
        source.setBio(null);
        source.setAvatarUrl(null);
        source.setDisplayName("Merged User");

        users.save(source);

        AdminUserMergeResultResponse resp = new AdminUserMergeResultResponse();
        resp.setSourcePublicRef(sourceRef);
        resp.setTargetPublicRef(targetRef);
        resp.setSourceDisabled(true);
        resp.setMergedInto(targetRef);
        resp.setAppliedChanges(changes);
        resp.setMergedAt(Instant.now());
        return resp;
    }

    private String tombstoneEmail(Long sourceId, String sourcePublicRef) {
        byte[] rnd = new byte[9];
        secureRandom.nextBytes(rnd);
        String nonce = urlEncoder.encodeToString(rnd);
        // Example: merged+<publicRef>.<nonce>@invalid.local
        // Keep it stable-ish + unique. Domain is reserved for internal/non-routable usage.
        String local = "merged+" + safe(sourcePublicRef) + "." + nonce + "." + (sourceId == null ? "x" : sourceId);
        String email = local + "@invalid.local";
        if (email.length() > 254) {
            // last resort truncate local part
            int maxLocal = 254 - "@invalid.local".length();
            email = local.substring(0, Math.max(1, maxLocal)) + "@invalid.local";
        }
        return email;
    }

    private String safe(String s) {
        if (s == null) return "na";
        // publicRef already constrained, but be defensive
        return s.replaceAll("[^A-Za-z0-9_-]", "x");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
