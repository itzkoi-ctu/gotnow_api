package com.itzkoictu.gotNow.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String country;
    private String state;
    private String city;
    private String street;
    private String mobileNumber;
    private String addressType;

}
