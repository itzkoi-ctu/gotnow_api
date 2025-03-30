package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.LoginRequest;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.ResponseError;
import com.itzkoictu.gotNow.security.jwt.JwtUtils;
import com.itzkoictu.gotNow.security.user.ShopUserDetailsService;
import com.itzkoictu.gotNow.service.email.EmailService;
import com.itzkoictu.gotNow.service.user.UserService;
import com.itzkoictu.gotNow.utils.CookieUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final EmailService emailService;
    @Value("${auth.token.refreshExpirationInMils}")
    private Long refreshTokenExpirationTime;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            Authentication authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            String accessToken= jwtUtils.generateAccessTokenForUser(authentication);
            String refreshToken= jwtUtils.generateRefreshToken(loginRequest.getEmail());
            cookieUtils.addRefreshTokenCookie(response, refreshToken, refreshTokenExpirationTime);


            Map<String, String> token= new HashMap<>();

            token.put("accessToken", accessToken);
            return ResponseEntity.accepted().body(token);
        } catch (Exception e) {
            throw new EntityNotFoundException("Invalid email or password");
        }

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

    @GetMapping("/oauth2/user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", oauth2User.getAttribute("name"));
        userDetails.put("email", oauth2User.getAttribute("email"));
        userDetails.put("picture", oauth2User.getAttribute("picture"));

        return ResponseEntity.ok(userDetails);
    }

    @RequestMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // Xóa session hiện tại
        }
        SecurityContextHolder.clearContext(); // Xóa authentication
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Gửi OTP qua email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        boolean isSent=emailService.sendResetPasswordOTP(email);
        return ResponseEntity.ok(
                ).body(new ApiResponse(HttpStatus.OK.value(), "OTP code has been sent via email", isSent));
    }

    /**
     * Xác minh OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        System.out.println("email "+ email+ " "+ otp);
        boolean isAuthenticatedOTP= emailService.verifyOTP(email,otp);
        if (isAuthenticatedOTP) {
            return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "OTP is authenticated", isAuthenticatedOTP));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "OTP is not authenticated or expired", isAuthenticatedOTP));
    }

    /**
     * Đặt lại mật khẩu
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        boolean isCompleted=emailService.resetPassword(email, newPassword);
        System.out.println("email "+ email+ " "+ newPassword);

        return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "Password reset successfully!", isCompleted));

    }


}
