package com.example.fridgemate.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class JwtUtil {

    private static final String KEY_FILE = "src/main/resources/keys/secret-key.dat";
    private static final SecretKey SECRET_KEY;

    static {
        try {
            SECRET_KEY = loadKey();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("Failed to load secret key", exception);
        }
    }

    private static SecretKey loadKey() throws IOException, ClassNotFoundException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(KEY_FILE));
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100L * 365 * 24 * 60 * 60 * 1000)) // Token expiration (100 years)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}