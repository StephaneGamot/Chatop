package com.chatop.backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "your_secret_key";

    public static String generateToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 900000)) // 15 minutes expiration time
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // You'll also need methods to validate and parse the token.
}
