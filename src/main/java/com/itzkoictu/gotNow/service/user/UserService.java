package com.itzkoictu.gotNow.service.user;


import com.itzkoictu.gotNow.dto.request.UserCreationRequest;
import com.itzkoictu.gotNow.dto.request.UserUpdateRequest;
import com.itzkoictu.gotNow.dto.response.UserResponse;
import com.itzkoictu.gotNow.enums.AuthProvider;
import com.itzkoictu.gotNow.model.Role;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.AddressRepository;
import com.itzkoictu.gotNow.repository.PasswordResetTokenRepository;
import com.itzkoictu.gotNow.repository.RoleRepository;
import com.itzkoictu.gotNow.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    public UserResponse createUser(UserCreationRequest request) {
        Role userRole= Optional.ofNullable(roleRepository.findByName("ROLE_USER"))
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(request1 -> {
                    User user = User.builder()
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .roles(Set.of(userRole))
                            .authProvider(AuthProvider.LOCAL)
                            .build();

                    User savedUser = userRepository.save(user);
                    Optional.ofNullable(request1.getAddressList()).ifPresent(addresses -> {
                        addresses.forEach(address -> {
                            address.setUser(savedUser);
                            addressRepository.save(address);
                        });
                    });
                    UserResponse userResponse= convertToUserResponse(savedUser);
                    return userResponse;


                }).orElseThrow(() -> new EntityExistsException("Oops! " + request.getEmail() + " already existed"));
    }

    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new EntityNotFoundException("user not found!"));
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
            throw new EntityNotFoundException("user not found!");
        });
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found!"));
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = mapper.map(user, UserResponse.class);
        return userResponse;

    }

    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        System.out.println("Email from auth: "+  email);
        User user= userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User dose not exists!"));
        System.out.println("Email "+ user.getEmail());
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Login required"));
    }

    public void updateUserAvatar(Long userId, String imageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarUrl(imageUrl);
        userRepository.save(user);
    }







}
