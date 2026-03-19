package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.AdminBootstrapRequest;
import com.um.springbootprojstructure.service.AdminBootstrapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-setup")
public class AdminBootstrapController {

    private final AdminBootstrapService bootstrapService;

    public AdminBootstrapController(AdminBootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @PostMapping("/bootstrap-admin")
    public ResponseEntity<?> bootstrapAdmin(@Valid @RequestBody AdminBootstrapRequest req) {
        bootstrapService.bootstrapAdmin(req.getSetupToken(), req.getEmail(), req.getPassword());
        // Do not return created user data
        return ResponseEntity.ok().build();
    }
}
