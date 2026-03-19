package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.PublicUserProfileResponse;
import com.um.springbootprojstructure.dto.UpdateUserProfileRequest;
import com.um.springbootprojstructure.dto.UserProfileResponse;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.mapper.UserMapper;
import com.um.springbootprojstructure.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        String email = authentication.getName();
        return UserMapper.toResponse(users.requireByEmail(email));
    }

    /**
     * Public profile lookup by a non-guessable publicRef.
     * Returns minimal safe profile data.
     */
    @GetMapping("/{publicRef}")
    public PublicUserProfileResponse getPublicProfile(
            @PathVariable
            @Pattern(regexp = "^[A-Za-z0-9_-]{10,64}$", message = "invalid")
            String publicRef
    ) {
        return UserMapper.toPublicProfile(users.requireByPublicRef(publicRef));
    }

    /**
     * Update allowed profile fields (owner or ADMIN).
     * Returns the updated profile.
     */
    @PutMapping("/{publicRef}")
    public UserProfileResponse updateProfile(
            Authentication authentication,
            @PathVariable
            @Pattern(regexp = "^[A-Za-z0-9_-]{10,64}$", message = "invalid")
            String publicRef,
            @Valid @RequestBody UpdateUserProfileRequest req
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        var updated = users.updateProfileByPublicRef(publicRef, req, authentication.getName(), isAdmin);
        return UserMapper.toUserProfile(updated);
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            if (role.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}
