package com.itzkoictu.gotNow.model;


import com.itzkoictu.gotNow.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

public class Address {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String country;
    String state;
    String city;
    String street;
    @Enumerated(EnumType.STRING)
    AddressType addressType;


    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;


}
