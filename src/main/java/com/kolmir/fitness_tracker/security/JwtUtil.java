package com.kolmir.fitness_tracker.security;

import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.kolmir.fitness_tracker.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
    
    public String extractUsername(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public Claims extractClaims(String token) {
        return parseToken(token).getPayload();
    }

    public String buildToken(Authentication authentication, long expiration) {
        User user = (User)authentication.getPrincipal();
        Instant expiry = Instant.now().plusMillis(expiration);
        
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        return buildToken(authentication, accessTokenExpiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        return buildToken(authentication, refreshTokenExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
        return isTokenExpired(token);
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        String username = claims.getSubject();
        
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenExpiration);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}
