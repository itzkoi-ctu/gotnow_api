package com.itzkoictu.gotNow.service.user;


import com.itzkoictu.gotNow.dto.request.UserCreationRequest;
import com.itzkoictu.gotNow.dto.request.UserUpdateRequest;
import com.itzkoictu.gotNow.dto.response.UserResponse;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    public UserResponse createUser(UserCreationRequest request){
            return Optional.of(request)
                    .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                    .map(request1 -> {
                        User user= User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();
                        userRepository.save(user);
                       return convertToUserResponse(user);
                    }).orElseThrow(()-> new EntityExistsException("Oops! "+request.getEmail()+ " already existed"));
    }
    public User updateUser(UserUpdateRequest request, Long userId){
       return userRepository.findById(userId).map(existingUser -> {
           existingUser.setFirstName(request.getFirstName());
           existingUser.setLastName(request.getLastName());
           return userRepository.save(existingUser);
        }).orElseThrow(()-> new EntityNotFoundException("user not found!"));
    }
    public void deleteUser(Long userId){
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () ->{
            throw new EntityNotFoundException("user not found!");
        });
    }
    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("user not found!"));
    }

    public UserResponse convertToUserResponse(User user){
        UserResponse userResponse= mapper.map(user, UserResponse.class);
        return userResponse;

    }

    public User getAuthenticated(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        String email= authentication.getName();

        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new EntityNotFoundException("Login required"));
    }
}
