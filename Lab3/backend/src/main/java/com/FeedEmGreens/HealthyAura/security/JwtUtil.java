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

//The purpose of this class is to generate tokens, validate it and extract information from the token(username and role)

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
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, String username){
        try{
            String tokenUser = extractUsername(token);
            return (tokenUser.equals(username)) && !isTokenExpired(token);
        } catch(JwtException e){
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }
}
