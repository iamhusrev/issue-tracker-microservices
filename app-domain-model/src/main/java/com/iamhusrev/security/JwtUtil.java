package com.iamhusrev.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtil(String secret, long accessTokenExpirationMs, long refreshTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(Long userId, String userName, Long organizationId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userName)
                .claim("userId", userId)
                .claim("organizationId", organizationId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId, String userName) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userName)
                .claim("userId", userId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public String getUserName(Claims claims) {
        return claims.getSubject();
    }

    public Long getOrganizationId(Claims claims) {
        return claims.get("organizationId", Long.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}
