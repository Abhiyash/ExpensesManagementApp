package com.apm.expenses.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    // This class acts as a lightweight token service to avoid introducing external JWT
    // dependencies in this environment. It generates secure tokens and stores them in-memory
    // with expiry. For production use replace with proper JWT signed tokens.
    private final String secret = "my_super_secret_key_32_characters_long_minimum!!";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String username, String userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600000)) // 1 hour expiry
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
