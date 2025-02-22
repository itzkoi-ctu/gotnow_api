package com.itzkoictu.gotNow.controller;


import com.itzkoictu.gotNow.dto.response.AddressResponse;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.model.Address;
import com.itzkoictu.gotNow.service.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/new")
    public ResponseEntity<ApiResponse> createAddresses(@RequestBody List<Address> addresses) {
        List<Address> addressList = addressService.createAddress(addresses);
        List<AddressResponse> addressDto = addressService.convertToResponse(addressList);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(),"Success!", addressDto));
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<ApiResponse> getUserAddresses(@PathVariable Long userId) {
        List<Address> addressList = addressService.getUserAddresses(userId);
        List<AddressResponse> addressDto = addressService.convertToResponse(addressList);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Found!", addressDto));
    }

    @GetMapping("/{id}/address")
    public ResponseEntity<ApiResponse> getAddressById(@PathVariable Long id) {
        Address address = addressService.getAddressById(id);
        AddressResponse addressDto = addressService.convertToAddressResponse(address);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Found!", addressDto));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        Address updatedAddress = addressService.updateUserAddress(id, address);
        AddressResponse addressDto = addressService.convertToAddressResponse(updatedAddress);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(),"Success!", addressDto));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Address deleted", null));
    }
}
