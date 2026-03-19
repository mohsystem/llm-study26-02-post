package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.XmlImportSummaryResponse;
import com.um.springbootprojstructure.service.AdminUserXmlImportService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserImportController {

    private final AdminUserXmlImportService importService;

    public AdminUserImportController(AdminUserXmlImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/import-xml", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public XmlImportSummaryResponse importXml(@RequestPart("file") @NotNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("invalid_xml");
        }
        // Do not trust original filename/content-type; parse bytes only.
        try (var in = file.getInputStream()) {
            return importService.importUsers(in);
        } catch (Exception e) {
            // Avoid leaking parser/internal details
            if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
            throw new IllegalArgumentException("invalid_xml");
        }
    }
}
