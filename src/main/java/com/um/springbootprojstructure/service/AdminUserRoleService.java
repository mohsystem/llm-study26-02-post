package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.UpdateUserRoleResponse;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserRoleService {

    private final UserRepository users;

    public AdminUserRoleService(UserRepository users) {
        this.users = users;
    }

    @Transactional
    public UpdateUserRoleResponse updateRole(long targetUserId, String requestedRole, String adminEmail) {
        if (requestedRole == null || requestedRole.isBlank()) {
            throw new IllegalArgumentException("validation_failed");
        }

        Role newRole;
        try {
            // Only allow explicit enum values. Fail closed on invalid input.
            newRole = Role.valueOf(requestedRole.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("invalid_role");
        }

        User admin = users.findByEmailIgnoreCase(adminEmail).orElseThrow(() -> new IllegalArgumentException("not_found"));

        User target = users.findById(targetUserId).orElseThrow(() -> new IllegalArgumentException("not_found"));

        if (!target.isEnabled()) {
            throw new IllegalArgumentException("user_disabled");
        }

        // Prevent admin self-demotion that could lock out administration.
        if (admin.getId().equals(target.getId()) && newRole != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("cannot_change_own_role");
        }

        if (target.getRole() != newRole) {
            target.setRole(newRole);
            users.save(target);
        }

        UpdateUserRoleResponse resp = new UpdateUserRoleResponse();
        resp.setUserId(target.getId());
        resp.setRole(target.getRole().name());
        return resp;
    }
}
