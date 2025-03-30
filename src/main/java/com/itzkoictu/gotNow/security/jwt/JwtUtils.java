package com.itzkoictu.gotNow.security.jwt;


import com.itzkoictu.gotNow.security.oauth2.UserPrincipal;
import com.itzkoictu.gotNow.security.user.ShopUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.accessExpirationInMils}")
    private String expirationTime;

    @Value("${auth.token.refreshExpirationInMils}")
    private String refreshExpirationTime;

//    public String generateAccessTokenForUser(Authentication authentication) {
//        ShopUserDetails userPrincipal = (ShopUserDetails) authentication.getPrincipal();
//
//        List<String> roles = userPrincipal.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority).toList();
//
//        return Jwts.builder()
//                .setSubject(userPrincipal.getEmail())
//                .claim("id", userPrincipal.getId())
//                .claim("roles", roles)
//                .setIssuedAt(new Date())
//                .setExpiration(calculateExpirationDate(expirationTime))
//                .signWith(key(), SignatureAlgorithm.HS256).compact();
//    }
public String generateAccessTokenForUser(Authentication authentication) {
    Object principal = authentication.getPrincipal();

    Long userId;
    String email;
    List<String> roles;

    if (principal instanceof UserPrincipal userPrincipal) {
        userId = userPrincipal.getId();
        email = userPrincipal.getEmail();
        roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    } else if (principal instanceof ShopUserDetails shopUserDetails) {
        userId = Long.valueOf(shopUserDetails.getId()); // Chuyển kiểu nếu cần
        email = shopUserDetails.getUsername();
        roles = shopUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    } else {
        throw new RuntimeException("Unsupported Principal Type: " + principal.getClass().getName());
    }

    return Jwts.builder()
            .setSubject(email)
            .claim("id", userId)
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(calculateExpirationDate(expirationTime))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
}


    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(calculateExpirationDate(refreshExpirationTime))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private Date calculateExpirationDate(String expirationTimeString) {
        long expirationTime = Long.parseLong(expirationTimeString); // Convert String to long
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }


    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id", Long.class); // Lấy userId từ token
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class); // Lấy danh sách roles từ token
    }


}
