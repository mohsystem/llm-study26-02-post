package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.UpdateUserRoleRequest;
import com.um.springbootprojstructure.dto.UpdateUserRoleResponse;
import com.um.springbootprojstructure.service.AdminUserRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserRoleController {

    private final AdminUserRoleService roleService;

    public AdminUserRoleController(AdminUserRoleService roleService) {
        this.roleService = roleService;
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UpdateUserRoleResponse updateRole(
            Authentication authentication,
            @PathVariable("id") @Min(1) long id,
            @Valid @RequestBody UpdateUserRoleRequest req
    ) {
        return roleService.updateRole(id, req.getRole(), authentication.getName());
    }
}
