package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.DeterministicDecisionResponse;
import com.um.springbootprojstructure.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<DeterministicDecisionResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        // Endpoint requires auth via SecurityConfig
        boolean ok = accountService.changePassword(authentication.getName(), req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.ok(new DeterministicDecisionResponse(ok));
    }
}
