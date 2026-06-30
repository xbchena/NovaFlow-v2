package com.novaflow.video.infra.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class LocalTokenService implements TokenService {

    private final ObjectMapper objectMapper;

    public LocalTokenService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateAccessToken(String userId) {
        return userId;
    }

    @Override
    public String generateRefreshToken(String userId) {
        return userId;
    }

    @Override
    public String validateAccessToken(String token) {
        return extractSubject(token);
    }

    @Override
    public String validateRefreshToken(String token) {
        return extractSubject(token);
    }

    private String extractSubject(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return token;
        }
        try {
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode json = objectMapper.readTree(new String(payload, StandardCharsets.UTF_8));
            JsonNode subject = json.get("sub");
            return subject == null ? token : subject.asText();
        } catch (Exception e) {
            return token;
        }
    }
}
