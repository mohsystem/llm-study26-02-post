package com.um.springbootprojstructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RateLimitFilter rateLimitFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        // Public auth endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()

                        // Password reset (public, deterministic responses)
                        .requestMatchers(HttpMethod.POST, "/api/auth/password-reset/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/password-reset/confirm").permitAll()

                        // Refresh + logout require authentication
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").hasAnyRole("USER", "ADMIN")

                        // Change password requires authentication
                        .requestMatchers(HttpMethod.POST, "/api/accounts/change-password").hasAnyRole("USER", "ADMIN")

                        // Test setup endpoint
                        .requestMatchers(HttpMethod.POST, "/api/test-setup/bootstrap-admin").permitAll()

                        // Public profile endpoint
                        .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()

                        // Update profile requires auth
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAnyRole("USER", "ADMIN")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Authenticated user endpoint
                        .requestMatchers("/api/users/me").hasAnyRole("USER", "ADMIN")

                        .anyRequest().denyAll()
                )
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
