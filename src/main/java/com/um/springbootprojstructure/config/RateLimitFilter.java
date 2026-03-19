package com.um.springbootprojstructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final class Counter {
        volatile long windowStartEpochSec;
        volatile int count;

        Counter(long windowStartEpochSec, int count) {
            this.windowStartEpochSec = windowStartEpochSec;
            this.count = count;
        }
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    private final int limitPerWindow = 40;
    private final long windowSeconds = 60;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(
                path.startsWith("/api/auth/login")
                        || path.startsWith("/api/auth/register")
                        || path.startsWith("/api/auth/refresh")
                        || path.startsWith("/api/auth/logout")
                        || path.startsWith("/api/auth/password-reset/request")
                        || path.startsWith("/api/auth/password-reset/confirm")
                        || path.startsWith("/api/test-setup/bootstrap-admin")
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = extractClientIp(request);
        long now = Instant.now().getEpochSecond();

        Counter c = counters.compute(ip, (k, old) -> {
            if (old == null || now - old.windowStartEpochSec >= windowSeconds) {
                return new Counter(now, 1);
            }
            old.count++;
            return old;
        });

        if (c.count > limitPerWindow) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"too_many_requests\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
