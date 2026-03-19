package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UpdateUserProfileRequest;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final PublicRefService publicRefService;
    private final PasswordRulesService passwordRulesService;

    public UserService(UserRepository users,
                       PasswordEncoder passwordEncoder,
                       PublicRefService publicRefService,
                       PasswordRulesService passwordRulesService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.publicRefService = publicRefService;
        this.passwordRulesService = passwordRulesService;
    }

    @Transactional
    public User registerUser(String email, String rawPassword) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("registration_not_allowed");
        }

        // Enforce active password rules
        var rules = passwordRulesService.getActiveRulesEntity();
        passwordRulesService.validatePasswordOrThrow(rawPassword, rules);

        String publicRef = allocateUniquePublicRef();
        String hash = passwordEncoder.encode(rawPassword);

        User u = new User(publicRef, email, hash, Role.ROLE_USER);
        return users.save(u);
    }

    @Transactional
    public User createAdmin(String email, String rawPassword) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("admin_setup_not_allowed");
        }

        // Enforce same rules for admin creation as well
        var rules = passwordRulesService.getActiveRulesEntity();
        passwordRulesService.validatePasswordOrThrow(rawPassword, rules);

        String publicRef = allocateUniquePublicRef();
        String hash = passwordEncoder.encode(rawPassword);

        User u = new User(publicRef, email, hash, Role.ROLE_ADMIN);
        return users.save(u);
    }

    @Transactional(readOnly = true)
    public User requireByEmail(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("not_found"));
    }

    @Transactional(readOnly = true)
    public User requireByPublicRef(String publicRef) {
        return users.findByPublicRef(publicRef)
                .filter(User::isEnabled) // only return enabled accounts publicly
                .orElseThrow(() -> new IllegalArgumentException("not_found"));
    }

    @Transactional(readOnly = true)
    public boolean anyAdminExists() {
        return users.findAll().stream().anyMatch(u -> u.getRole() == Role.ROLE_ADMIN);
    }

    /**
     * Update allowed fields only. Authorization:
     * - ADMIN can update any enabled user's profile
     * - USER can update own profile only
     *
     * To reduce user enumeration, returns "not_found" if:
     * - target doesn't exist/enabled OR
     * - requester isn't allowed to update it
     */
    @Transactional
    public User updateProfileByPublicRef(String targetPublicRef, UpdateUserProfileRequest req,
                                        String requesterEmail, boolean requesterIsAdmin) {

        User requester = requireByEmail(requesterEmail);

        User target = users.findByPublicRef(targetPublicRef)
                .filter(User::isEnabled)
                .orElseThrow(() -> new IllegalArgumentException("not_found"));

        boolean isOwner = requester.getId().equals(target.getId());
        boolean allowed = requesterIsAdmin || isOwner;

        if (!allowed) {
            // Avoid confirming target existence to unauthorized callers
            throw new IllegalArgumentException("not_found");
        }

        // Apply updates (partial update semantics: only non-null fields)
        if (req.getDisplayName() != null) {
            target.setDisplayName(req.getDisplayName().trim());
        }
        if (req.getBio() != null) {
            String bio = req.getBio().trim();
            target.setBio(bio.isEmpty() ? null : bio);
        }
        if (req.getAvatarUrl() != null) {
            String url = req.getAvatarUrl().trim();
            target.setAvatarUrl(url.isEmpty() ? null : url);
        }

        return users.save(target);
    }

    private String allocateUniquePublicRef() {
        // retry on extremely unlikely collision
        for (int i = 0; i < 5; i++) {
            String ref = publicRefService.newPublicRef();
            if (!users.existsByPublicRef(ref)) {
                return ref;
            }
        }
        throw new IllegalStateException("public_ref_generation_failed");
    }
}
