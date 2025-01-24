package com.example.demo.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    public static final String SECRET_KEY = "thisisaverysecretkey12345678900987";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60; //1 hour

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    //Token Generation
    public String generateToken(String username, Map<String,Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Validate the token
    public boolean ValidateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignedKey()).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // Username extraction

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignedKey()).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Map<String,Object> extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignedKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }
}
