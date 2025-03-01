package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.UserCreationRequest;
import com.itzkoictu.gotNow.dto.request.UserUpdateRequest;
import com.itzkoictu.gotNow.dto.response.AddressResponse;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.UserResponse;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.service.address.AddressService;
import com.itzkoictu.gotNow.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    private final AddressService addressService;
    @GetMapping("/user/{userId}/user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId){
        User user= userService.getUserById(userId);
        UserResponse userResponse= userService.convertToUserResponse(user);
        List<AddressResponse> addressResponse= addressService.convertToResponse(user.getAddresses());
        userResponse.setAddressList(addressResponse);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success!", userResponse));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser( @Valid @RequestBody UserCreationRequest request){
        UserResponse userResponse= userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "created successfully!", userResponse));
    }
    @PutMapping("/user/update/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId){
        User user= userService.updateUser(request, userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "updated successfully!", user));
    }
    @DeleteMapping("user/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
         userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "deleted successfully!"));
    }
}
