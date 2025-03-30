package com.itzkoictu.gotNow.service.email;


import com.itzkoictu.gotNow.model.PasswordResetToken;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.PasswordResetTokenRepository;
import com.itzkoictu.gotNow.repository.UserRepository;
import com.itzkoictu.gotNow.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;



    /**
     * Gửi OTP qua email
     */
    @Transactional
    public boolean sendResetPasswordOTP(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User dose not exists!"));

        // Xóa OTP cũ nếu có
        tokenRepository.deleteByEmail(email);

        // Tạo mã OTP ngẫu nhiên 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Lưu OTP vào database với thời gian hết hạn là 5 phút
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(otp);
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        tokenRepository.save(passwordResetToken);

        // Gửi OTP qua email
        String subject = "OTP code to reset password";
        String body = "Your OTP code is: " + otp + ". This code is valid for 5 minutes.";

        sendEmail(email, subject, body);
        return true;
    }

    /**
     * Kiểm tra OTP
     */
    public boolean verifyOTP(String email, String otp) {
        PasswordResetToken resetToken = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP does not exist"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }
        if (resetToken.getToken().equals(otp)) {
            resetToken.setVerified(true);
            tokenRepository.save(resetToken);
            return true;
        }
        return false;
    }

    /**
     * Đặt lại mật khẩu mới
     */
    public boolean resetPassword(String email, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP does not exist"));

        if (!resetToken.isVerified()) {
            throw new RuntimeException("OTP not authenticated");
        }

        User user= userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User dose not exists!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa OTP sau khi đặt lại mật khẩu
        tokenRepository.deleteByEmail(email);
        return true;
    }

    /**
     * Gửi email bằng JavaMailSender
     */
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
