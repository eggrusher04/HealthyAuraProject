package com.FeedEmGreens.HealthyAura.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest; // <-- NEW IMPORT
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

// The purpose of this class is to generate tokens, validate it and extract information from the token (username and role)

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expiry;

    private SecretKey key;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role){
        Instant now = Instant.now();
        Instant exp = now.plus(expiry, ChronoUnit.MILLIS);

        return Jwts.builder()
                .claims()
                .subject(username)
                .add("role",role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) throws JwtException{
        // NOTE: setSigningKey is deprecated in modern JJWT.
        // It's recommended to use Jwts.parser().verifyWith(key) or Jwts.parser().decryptWith(key).
        // For compatibility with your existing code, I'll update this slightly.
        return Jwts.parser()
                .verifyWith(key) // Use verifyWith for a SecretKey
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Renamed to match the method name the AdminAuthController was looking for.
    public String getUsernameFromToken(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    // Renamed and simplified to match the method name the AdminAuthController was looking for: validateToken(String)
    public boolean validateToken(String token){
        try{
            // Simply parsing the token will validate its signature and check its expiration time.
            extractAllClaims(token);
            return true;
        } catch(JwtException e){
            // Log the exception for debugging purposes
            System.err.println("JWT Validation Error: " + e.getMessage());
            return false;
        }
    }
    
    // NOTE: The previous isTokenValid is now redundant but kept private for reference
    /*
    private boolean isTokenValid(String token, String username){
        try{
            String tokenUser = getUsernameFromToken(token); // Use new method name
            return (tokenUser.equals(username)) && validateToken(token); // Use new method name
        } catch(JwtException e){
            return false;
        }
    }
    */
    
    // The AdminAuthController requires this method to pull the token from the header.
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    // Kept private helper method
    private boolean isTokenExpired(String token){
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }
}