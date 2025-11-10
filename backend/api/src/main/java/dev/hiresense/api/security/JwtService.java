package dev.hiresense.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long expiryMinutes;

    public JwtService(String secret, String issuer, long expiryMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.expiryMinutes = expiryMinutes;
    }

    public String issue(String subject) {
        Instant now = Instant.now();
        return Jwts.builder().subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiryMinutes * 60)))
                .signWith(key)
                .compact();
    }

    public String parseSubject(String token) {
        try {
            var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return claims.getPayload().getSubject();
        } catch (Exception e) { return null; }
    }
}
