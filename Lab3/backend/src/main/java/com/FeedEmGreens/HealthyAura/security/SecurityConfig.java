package com.FeedEmGreens.HealthyAura.security;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration class that defines how authentication
 * and authorization are handled throughout the HealthyAura backend.
 *
 * <p>This configuration ensures that:
 * <ul>
 *   <li>JWT-based authentication is applied globally</li>
 *   <li>Endpoints are authorized based on user roles</li>
 *   <li>CORS policies allow secure frontend-backend communication</li>
 *   <li>Session management is stateless (since JWT handles user sessions)</li>
 * </ul>
 * </p>
 *
 * <p>All security filters and access control rules are defined here.
 * Public endpoints (e.g., {@code /auth/login}, {@code /auth/signup})
 * are explicitly whitelisted, while all others require authentication.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.security.JwtAuthenticationFilter
 * @see com.FeedEmGreens.HealthyAura.security.JwtUtil
 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity
 * @see org.springframework.security.web.SecurityFilterChain
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /** Custom filter for JWT validation before controller access. */
    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Constructs the security configuration and injects the custom JWT filter.
     *
     * @param jwtFilter the {@link JwtAuthenticationFilter} used for validating tokens
     */
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Defines the primary security filter chain configuration.
     *
     * <p>This method sets up:
     * <ul>
     *   <li><b>CORS</b> — allows frontend (React app) communication via HTTP</li>
     *   <li><b>CSRF disabled</b> — since JWT already ensures request integrity</li>
     *   <li><b>Authorization rules</b> — defines public and restricted endpoints</li>
     *   <li><b>Stateless sessions</b> — each request is self-contained via JWT</li>
     *   <li><b>JWT filter</b> — processes tokens before {@link UsernamePasswordAuthenticationFilter}</li>
     * </ul>
     * </p>
     *
     * @param http the {@link HttpSecurity} configuration builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during security setup
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 frames
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/login").permitAll()
                        .requestMatchers("/auth/admin/signup").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore((Filter) jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) to allow frontend requests.
     *
     * <p>By default, allows HTTP requests from <b>http://localhost:3000</b>,
     * which corresponds to the React frontend during development.</p>
     *
     * @return a {@link CorsConfigurationSource} specifying allowed origins, methods, and headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Exposes the {@link AuthenticationManager} bean used by Spring Security
     * for user authentication management.
     *
     * <p>This bean integrates seamlessly with the JWT authentication flow,
     * supporting login operations within {@link com.FeedEmGreens.HealthyAura.service.AuthService}.</p>
     *
     * @param config the Spring {@link AuthenticationConfiguration} used to retrieve the manager
     * @return the configured {@link AuthenticationManager} instance
     * @throws Exception if an error occurs while obtaining the authentication manager
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
