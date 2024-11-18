package com.example.rental.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.String;

@Slf4j
@Component
public class JwtCore {

    @Value("${testing.app.secret}")
    private String secret;

    @Value("${testing.app.expiration}")
    private long expiration;

    Map<String, Object> claims = new HashMap<>();

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 bytes for HS512");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(expiration);

        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("username", userDetails.getUsername());
        claims.put("roles", rolesList);


        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getNameFromToken(String token) {
        log.info("Token to parse: {}", token);
        Claims claims = getAllClaimsFromToken(token);
        log.info("Parsed claims: {}", claims);

        String username = claims.get("username", String.class);
        log.info("Extracted username: {}", username);
        return username;
    }


    public List<String> getRoles(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List<?> roles = claims.get("roles", List.class);
        return roles.stream()
                .filter(role -> role instanceof String)
                .map(role -> (String) role)
                .collect(Collectors.toList());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
