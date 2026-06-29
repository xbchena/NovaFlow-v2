package com.novaflow.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "novaflow-jwt-secret-change-me-please-256bit-key";
    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET);
    }

    @Test
    void parseAndValidate_returnsUserId_forValidToken() {
        String token = tokenFor("user-123", 60_000);
        assertThat(provider.parseAndValidate(token)).isEqualTo("user-123");
    }

    @Test
    void parseAndValidate_returnsNull_forTamperedToken() {
        String token = tokenFor("user-123", 60_000) + "x";
        assertThat(provider.parseAndValidate(token)).isNull();
    }

    @Test
    void parseAndValidate_returnsNull_forExpiredToken() {
        String token = tokenFor("user-123", -60_000); // 已过期
        assertThat(provider.parseAndValidate(token)).isNull();
    }

    @Test
    void parseAndValidate_returnsNull_whenSecretDiffers() {
        JwtTokenProvider other = new JwtTokenProvider("another-secret-key-also-long-enough-256bit!!!");
        String token = tokenFor("user-123", 60_000);
        assertThat(other.parseAndValidate(token)).isNull();
    }

    private String tokenFor(String subject, long ttlMillis) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMillis))
                .signWith(key)
                .compact();
    }
}
