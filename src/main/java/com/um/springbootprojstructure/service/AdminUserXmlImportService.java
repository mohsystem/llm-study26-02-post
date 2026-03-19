package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.XmlImportSummaryResponse;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AdminUserXmlImportService {

    private final SecureXmlParser secureXmlParser;
    private final UserRepository users;
    private final PublicRefService publicRefService;
    private final PasswordEncoder passwordEncoder;

    private final long maxBytes;
    private final int maxRecords;
    private final boolean skipExistingEmail;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public AdminUserXmlImportService(
            SecureXmlParser secureXmlParser,
            UserRepository users,
            PublicRefService publicRefService,
            PasswordEncoder passwordEncoder,
            @Value("${security.import.xml.max-bytes}") long maxBytes,
            @Value("${security.import.xml.max-records}") int maxRecords,
            @Value("${security.import.xml.skip-existing-email:true}") boolean skipExistingEmail
    ) {
        this.secureXmlParser = secureXmlParser;
        this.users = users;
        this.publicRefService = publicRefService;
        this.passwordEncoder = passwordEncoder;
        this.maxBytes = maxBytes;
        this.maxRecords = maxRecords;
        this.skipExistingEmail = skipExistingEmail;
    }

    /**
     * Parses legacy XML, creates USER accounts, returns summary.
     * Password handling:
     * - We DO NOT import plaintext passwords.
     * - We DO NOT accept unknown legacy hashes by default.
     * - We generate a random temporary password hash (not returned).
     *
     * Operationally, you should add a "reset password" flow and require users to set a new password.
     */
    @Transactional
    public XmlImportSummaryResponse importUsers(InputStream xmlStream) {
        byte[] xmlBytes = readAllWithLimit(xmlStream, maxBytes);

        Document doc = secureXmlParser.parse(new ByteArrayInputStream(xmlBytes));

        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new IllegalArgumentException("invalid_xml");
        }

        NodeList userNodes = root.getElementsByTagName("user");
        int total = Math.min(userNodes.getLength(), maxRecords);

        int imported = 0;
        int skipped = 0;
        int rejected = 0;
        List<String> details = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            Node n = userNodes.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                rejected++;
                addDetail(details, i, "invalid_record");
                continue;
            }
            Element e = (Element) n;

            String email = textOf(e, "email");
            String displayName = textOf(e, "displayName");
            String bio = textOf(e, "bio");
            String avatarUrl = textOf(e, "avatarUrl");

            // Validate email presence and length (reuse constraints similar to RegisterRequest)
            if (email == null || email.isBlank() || email.length() > 254 || !looksLikeEmail(email)) {
                rejected++;
                addDetail(details, i, "invalid_email");
                continue;
            }

            // Check existing
            if (users.existsByEmailIgnoreCase(email)) {
                if (skipExistingEmail) {
                    skipped++;
                } else {
                    rejected++;
                    addDetail(details, i, "email_exists");
                }
                continue;
            }

            // Create user (role USER only for import)
            String publicRef = allocateUniquePublicRef();
            String tempPasswordHash = passwordEncoder.encode(generateTempSecret());

            User u = new User(publicRef, email, tempPasswordHash, Role.ROLE_USER);

            // Apply optional profile fields with conservative validation
            if (displayName != null) {
                displayName = displayName.trim();
                if (!displayName.isEmpty() && displayName.length() <= 80) {
                    // allow a limited safe charset (similar to update DTO)
                    if (displayName.matches("^[\\p{L}\\p{N} .,'-]{1,80}$")) {
                        u.setDisplayName(displayName);
                    }
                }
            }
            if (bio != null) {
                bio = bio.trim();
                if (!bio.isEmpty() && bio.length() <= 500) {
                    u.setBio(bio);
                }
            }
            if (avatarUrl != null) {
                avatarUrl = avatarUrl.trim();
                if (!avatarUrl.isEmpty() && avatarUrl.length() <= 300 && avatarUrl.matches("^(https?://).*$")) {
                    u.setAvatarUrl(avatarUrl);
                }
            }

            users.save(u);
            imported++;
        }

        // If the XML contained more than maxRecords, count the rest as rejected (policy choice)
        if (userNodes.getLength() > maxRecords) {
            int extra = userNodes.getLength() - maxRecords;
            rejected += extra;
            addDetail(details, maxRecords, "too_many_records_truncated");
        }

        XmlImportSummaryResponse resp = new XmlImportSummaryResponse();
        resp.setTotalRecords(Math.min(userNodes.getLength(), maxRecords));
        resp.setImported(imported);
        resp.setSkipped(skipped);
        resp.setRejected(rejected);
        resp.setRejectionDetails(limitDetails(details, 200));
        return resp;
    }

    private byte[] readAllWithLimit(InputStream in, long limitBytes) {
        try {
            if (limitBytes <= 0 || limitBytes > 20_000_000L) {
                // fail closed if misconfigured (avoid OOM)
                throw new IllegalStateException("service_unavailable");
            }
            byte[] buffer = new byte[8192];
            int read;
            long total = 0;
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (total > limitBytes) {
                    throw new IllegalArgumentException("file_too_large");
                }
                bout.write(buffer, 0, read);
            }
            return bout.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid_xml");
        }
    }

    private String textOf(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() == 0) return null;
        Node n = nl.item(0);
        if (n == null) return null;
        String t = n.getTextContent();
        return t == null ? null : t.trim();
    }

    private boolean looksLikeEmail(String email) {
        // Conservative basic check; register endpoint uses @Email validation already.
        return email.contains("@") && email.indexOf('@') > 0 && email.indexOf('@') < email.length() - 3;
    }

    private String allocateUniquePublicRef() {
        for (int i = 0; i < 5; i++) {
            String ref = publicRefService.newPublicRef();
            if (!users.existsByPublicRef(ref)) {
                return ref;
            }
        }
        throw new IllegalStateException("public_ref_generation_failed");
    }

    private String generateTempSecret() {
        // 24 bytes ~ 32 chars base64url, high entropy. Not returned or logged.
        byte[] b = new byte[24];
        secureRandom.nextBytes(b);
        return encoder.encodeToString(b);
    }

    private void addDetail(List<String> details, int index, String reason) {
        // Do not include email or PII in details.
        details.add(index + ":" + reason);
    }

    private List<String> limitDetails(List<String> details, int max) {
        if (details.size() <= max) return details;
        return details.subList(0, max);
    }
}
