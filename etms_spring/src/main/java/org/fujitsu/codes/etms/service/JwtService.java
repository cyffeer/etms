package org.fujitsu.codes.etms.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtService(
            @Value("${etms.jwt.secret}") String secret,
            @Value("${etms.jwt.expiration-minutes:480}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generateToken(Login login) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expiration);
        UserRole role = login.getRole() == null ? UserRole.EMPLOYEE : login.getRole();

        return Jwts.builder()
                .subject(login.getUsername())
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public UserRole extractRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        return UserRole.fromValue(role);
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (RuntimeException ex) {
            throw new InvalidInputException("JWT token is invalid or expired");
        }
    }
}
