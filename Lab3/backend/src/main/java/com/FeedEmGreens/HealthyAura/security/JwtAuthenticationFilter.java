package com.FeedEmGreens.HealthyAura.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Security filter responsible for validating JSON Web Tokens (JWT)
 * in incoming HTTP requests before they reach the controller layer.
 *
 * <p>This filter ensures that each request is authenticated by verifying
 * the JWT provided in the {@code Authorization} header. If the token is valid,
 * user details (such as username and role) are extracted and stored in
 * Spring Security’s {@link SecurityContextHolder} for downstream access control.</p>
 *
 * <p>Requests to public endpoints such as <b>/auth/login</b> and <b>/auth/signup</b>
 * are excluded from token validation to allow unauthenticated access.</p>
 *
 * <p>This class extends {@link OncePerRequestFilter} to guarantee that
 * token validation occurs exactly once per request lifecycle.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.security.JwtUtil
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.web.authentication.WebAuthenticationDetailsSource
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Utility class responsible for token parsing and validation. */
    private final JwtUtil jwtUtil;

    /**
     * Constructs the JWT authentication filter with the specified utility.
     *
     * @param jwtUtil the {@link JwtUtil} instance used for token validation and claim extraction
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Core filter logic executed once per request.
     * <p>
     * This method intercepts all incoming requests, extracts the {@code Authorization}
     * header, validates the JWT, and populates the {@link SecurityContextHolder}
     * with the authenticated user's credentials and role if verification succeeds.
     * </p>
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Skip filtering for public endpoints (login/signup).</li>
     *   <li>Extract token from the {@code Authorization: Bearer &lt;token&gt;} header.</li>
     *   <li>Validate and parse claims using {@link JwtUtil}.</li>
     *   <li>Set up {@link UsernamePasswordAuthenticationToken} in the security context.</li>
     *   <li>Continue the filter chain regardless of outcome.</li>
     * </ol>
     * </p>
     *
     * @param request  the incoming {@link HttpServletRequest}
     * @param response the outgoing {@link HttpServletResponse}
     * @param chain    the {@link FilterChain} to continue request processing
     * @throws ServletException if an error occurs during the filter process
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Skip filtering for public endpoints
        String path = request.getServletPath();
        if (path.startsWith("/auth/login") || path.startsWith("/auth/signup")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null,
                            Collections.singleton(() -> "ROLE_" + role));

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
