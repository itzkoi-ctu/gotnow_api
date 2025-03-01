package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.LoginRequest;
import com.itzkoictu.gotNow.dto.response.ResponseError;
import com.itzkoictu.gotNow.security.jwt.JwtUtils;
import com.itzkoictu.gotNow.security.user.ShopUserDetailsService;
import com.itzkoictu.gotNow.utils.CookieUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final ShopUserDetailsService shopUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @Value("${auth.token.refreshExpirationInMils}")
    private Long refreshTokenExpirationTime;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        Authentication authentication= authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String accessToken= jwtUtils.generateAccessTokenForUser(authentication);
        String refreshToken= jwtUtils.generateRefreshToken(loginRequest.getEmail());
        cookieUtils.addRefreshTokenCookie(response, refreshToken, refreshTokenExpirationTime);


        Map<String, String> token= new HashMap<>();

        token.put("accessToken", accessToken);
        return ResponseEntity.accepted().body(token);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenAccess(HttpServletRequest request){
        cookieUtils.logCookies(request);
        String refreshToken= cookieUtils.getRefreshTokenFromCookies(request);
        if(refreshToken != null){
            boolean isValid= jwtUtils.validateToken(refreshToken);
            if(isValid){
                String usernameFromToken= jwtUtils.getUsernameFromToken(refreshToken);
                UserDetails userDetails= shopUserDetailsService.loadUserByUsername(usernameFromToken);
                String newAccessToken= jwtUtils.generateAccessTokenForUser(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                if(newAccessToken != null){
                    Map<String, String> token= new HashMap<>();
                    token.put("accessToken", newAccessToken);
                    return ResponseEntity.ok(token);
                }else {
                    return ResponseEntity
                            .status(500)
                            .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error generating new access token"));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid of expire access token");
    }

}
