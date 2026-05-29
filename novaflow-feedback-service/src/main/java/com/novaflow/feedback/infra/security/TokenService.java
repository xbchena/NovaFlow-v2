package com.novaflow.feedback.infra.security;

/**
 * TokenService stub interface for feedback service.
 * Delegates to the auth service via network calls in production.
 */
public interface TokenService {

    /**
     * Generate an access token for the given user ID.
     *
     * @param userId the user ID
     * @return the generated access token
     */
    String generateAccessToken(String userId);

    /**
     * Generate a refresh token for the given user ID.
     *
     * @param userId the user ID
     * @return the generated refresh token
     */
    String generateRefreshToken(String userId);

    /**
     * Validate an access token and return the user ID.
     *
     * @param token the access token to validate
     * @return the user ID extracted from the token
     */
    String validateAccessToken(String token);

    /**
     * Validate a refresh token and return the user ID.
     *
     * @param token the refresh token to validate
     * @return the user ID extracted from the token
     */
    String validateRefreshToken(String token);
}
