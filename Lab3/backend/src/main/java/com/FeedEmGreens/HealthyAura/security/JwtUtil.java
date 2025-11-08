package com.FeedEmGreens.HealthyAura.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class responsible for generating, validating, and parsing JSON Web Tokens (JWT)
 * used in the HealthyAura authentication system.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Token creation with username and role claims</li>
 *   <li>Validation of token integrity and expiration</li>
 *   <li>Extraction of claims such as username and role</li>
 * </ul>
 * </p>
 *
 * <p>It uses a Base64-encoded secret key (configured in {@code application.properties})
 * and the HMAC-SHA256 signing algorithm to ensure token security and authenticity.</p>
 *
 * @see io.jsonwebtoken.Jwts
 * @see com.FeedEmGreens.HealthyAura.security.JwtAuthenticationFilter
 * @see org.springframework.security.core.context.SecurityContextHolder
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Component
public class JwtUtil {

    /** Base64-encoded secret key for signing and verifying JWTs. */
    @Value("${jwt.secret}")
    private String base64Secret;

    /** Token expiration time in milliseconds (default: 1 hour). */
    @Value("${jwt.expiration-ms:3600000}")
    private long expiry;

    /** SecretKey generated from the Base64 secret for HMAC signing. */
    private SecretKey key;

    /**
     * Initializes the {@link SecretKey} from the Base64-encoded secret
     * after dependency injection.
     *
     * <p>This method runs automatically after the component is constructed,
     * ensuring that the JWT signing key is ready for use.</p>
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT containing the given username and role.
     *
     * <p>The token includes:
     * <ul>
     *   <li>{@code sub} (subject) — the username</li>
     *   <li>{@code role} — the user’s role (e.g., USER or ADMIN)</li>
     *   <li>Issued and expiration timestamps</li>
     * </ul>
     * </p>
     *
     * @param username the username to embed in the token
     * @param role the user’s role to embed as a claim
     * @return a signed JWT string
     */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(expiry, ChronoUnit.MILLIS);

        return Jwts.builder()
                .claims()
                .subject(username)
                .add("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts all claims from a JWT token after verifying its signature.
     *
     * @param token the JWT to parse
     * @return the decoded {@link Claims} object containing all token data
     * @throws JwtException if the token is invalid or cannot be verified
     */
    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the username (subject) from the token.
     *
     * @param token the JWT string
     * @return the username stored in the {@code sub} claim
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the role claim from the token.
     *
     * @param token the JWT string
     * @return the user role (e.g., "USER" or "ADMIN")
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validates a token against the given username and ensures it is not expired.
     *
     * <p>This method verifies that:
     * <ul>
     *   <li>The token’s subject matches the provided username.</li>
     *   <li>The token has not yet expired.</li>
     * </ul>
     * Returns {@code false} if validation fails or any parsing error occurs.</p>
     *
     * @param token the JWT string
     * @param username the expected username
     * @return {@code true} if valid and unexpired, otherwise {@code false}
     */
    public boolean isTokenValid(String token, String username) {
        try {
            String tokenUser = extractUsername(token);
            return (tokenUser.equals(username)) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Determines whether the token has expired based on its {@code exp} claim.
     *
     * @param token the JWT string
     * @return {@code true} if the token is expired, otherwise {@code false}
     */
    private boolean isTokenExpired(String token) {
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }
}
