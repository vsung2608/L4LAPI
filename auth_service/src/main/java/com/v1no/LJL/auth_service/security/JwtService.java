package com.v1no.LJL.auth_service.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.model.entity.UserCredential;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.expirations.access-token}")
    private long jwtExpiration;

    @Value("${application.security.jwt.expirations.refresh-token}")
    private long refreshExpiration;

    @Value("${application.security.jwt.secrets}")
    private String jwtSecretKey;

    private String buildToken(
        Map<String, Object> extraClaims, 
        UserCredential userCredential, 
        long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userCredential.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .id(UUID.randomUUID().toString())
                .signWith(getKey())
                .compact();
    }

    public String generateAccessToken(UserCredential userCredential) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userCredential.getId());
        claims.put("role", userCredential.getRole());
        claims.put("type", "ACCESS_TOKEN");
        
        return buildToken(claims, userCredential, jwtExpiration);
    }

    public String generateRefreshToken(UserCredential userCredential) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH_TOKEN");
        
        return buildToken(claims, userCredential, refreshExpiration);
    }

    public String buildToken(HashMap<String, Object> claims, UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey())
                .claim("authorities", authorities)
                .compact()
                ;
    }

    private Key getKey() {
        byte[] keyByte = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public <T> T extractClaims(String token, Function<Claims, T> function){
        final Claims claims = extractAllClaims(token);
        return function.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
            .verifyWith((SecretKey) getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var email = extractUsername(token);

        return (!isTokenExpired(token) && email.equals(userDetails.getUsername()));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public void addTokenToBlackList(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String jti = claims.getId(); // Lấy JTI chuẩn

            if (jti == null) {
                log.warn("Token does not have a JTI, using subject as fallback (not recommended)");
                jti = claims.getSubject();
            }

            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                log.info("Token JTI: {} added to blacklist", jti);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    // public boolean isTokenInBlackList(String token){
    //     try {
    //         Claims claims = extractAllClaims(token);
    //         String tokenId = claims.get("tokenId").toString();

    //         Boolean exists = redisTemplate.hasKey(tokenId);
    //         return exists != null && exists;

    //     } catch (Exception e) {
    //         log.warn("Error checking token blacklist status: {}", e.getMessage());
    //         return false;
    //     }
    // }
}