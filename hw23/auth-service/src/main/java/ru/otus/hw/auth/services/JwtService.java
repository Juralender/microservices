package ru.otus.hw.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import ru.otus.hw.auth.config.JwtProperties;
import ru.otus.hw.auth.exceptions.InvalidTokenException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String CLAIM_USER_ID = "uid";

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtService(JwtProperties jwtProperties) {
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(jwtProperties.getExpirationMinutes());
    }

    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }
}
