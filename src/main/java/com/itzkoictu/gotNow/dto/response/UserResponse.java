package com.itzkoictu.gotNow.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private List<OrderResponse> orders;
    private CartResponse cart;
    private List<AddressResponse> addressList;
}
