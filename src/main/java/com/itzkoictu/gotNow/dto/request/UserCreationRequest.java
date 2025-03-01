package com.itzkoictu.gotNow.dto.request;

import com.itzkoictu.gotNow.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data

public class UserCreationRequest {
//    @NotBlank(message = "firstName must be not blank")
    private String firstName;
//    @NotBlank(message = "lastName must be not blank")
    private String lastName;
//    @Email(message = "email invalid format")
    private String email;
//    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    private List<Address> addressList;
}
