package com.itzkoictu.gotNow.security.oauth2;

import com.itzkoictu.gotNow.enums.AuthProvider;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        // Kiểm tra email có tồn tại không
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email không được tìm thấy từ OAuth2 provider");
        }

        Optional<User> userOptional =userRepository.findByEmail(userInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            // Người dùng đã tồn tại, cập nhật thông tin nếu cần
            user = userOptional.get();
            user = updateExistingUser(user, userInfo);
        } else {
            // Đăng ký người dùng mới
            user = registerNewUser(userRequest, userInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest userRequest, GoogleOAuth2UserInfo userInfo) {
        String email = userInfo.getEmail();
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                throw new RuntimeException("This email has already been registered with a regular account. Please log in with your password!");
            }
            return user; // Trường hợp đã đăng ký bằng Google, trả về user
        }

        // Nếu email chưa tồn tại, tiến hành tạo user mới
        User newUser = new User();
        String fullName = userInfo.getName();

        int lastIndex = fullName.lastIndexOf(" ");
        String firstName;
        String lastName;

        if (lastIndex == -1) {
            firstName = fullName;
            lastName = "";
        } else {
            firstName = fullName.substring(0, lastIndex).trim();
            lastName = fullName.substring(lastIndex + 1).trim();
        }

        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setAvatarUrl(userInfo.getImageUrl());
        newUser.setAuthProvider(AuthProvider.GOOGLE);

        return userRepository.save(newUser);
    }



    private User updateExistingUser(User existingUser, GoogleOAuth2UserInfo userInfo) {
        existingUser.setFirstName(userInfo.getName());
        existingUser.setAvatarUrl(userInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}