package com.um.springbootprojstructure.mapper;

import com.um.springbootprojstructure.dto.UserProfileResponse;
import com.um.springbootprojstructure.dto.PublicUserProfileResponse;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.entity.User;

public final class UserMapper {
    private UserMapper() {}

    public static UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setPublicRef(u.getPublicRef());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        r.setEnabled(u.isEnabled());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }

    public static PublicUserProfileResponse toPublicProfile(User u) {
        PublicUserProfileResponse r = new PublicUserProfileResponse();
        r.setPublicRef(u.getPublicRef());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }

    public static UserProfileResponse toUserProfile(User u) {
        UserProfileResponse r = new UserProfileResponse();
        r.setPublicRef(u.getPublicRef());
        r.setDisplayName(u.getDisplayName());
        r.setBio(u.getBio());
        r.setAvatarUrl(u.getAvatarUrl());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
