package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.AdminUserMergeRequest;
import com.um.springbootprojstructure.dto.AdminUserMergeResultResponse;
import com.um.springbootprojstructure.service.AdminUserMergeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserMergeService mergeService;

    public AdminUserController(AdminUserMergeService mergeService) {
        this.mergeService = mergeService;
    }

    @PostMapping("/merge")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserMergeResultResponse merge(@Valid @RequestBody AdminUserMergeRequest req) {
        return mergeService.mergeByPublicRefs(req.getSourcePublicRef(), req.getTargetPublicRef());
    }
}
