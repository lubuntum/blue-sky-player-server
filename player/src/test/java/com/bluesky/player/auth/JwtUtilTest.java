package com.bluesky.player.auth;

import com.bluesky.player.database.entity.account.Account;
import com.bluesky.player.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {
    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secretKey = "test-secret-key-1234567890-1234567890-1234567890";
    private final String testEmail = "test@example.com";
    private SecretKey getSigningKey(){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
    }
    @Test
    void generateToken_WithExtraClaims_ShouldGenerateValidToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", 123);
        Account userDetails = mock(Account.class);
        when(userDetails.getEmail()).thenReturn(testEmail);

        String token = jwtUtil.generateToken(extraClaims, userDetails);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testEmail, claims.getSubject());
        assertEquals(123, claims.get("id"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }


}
