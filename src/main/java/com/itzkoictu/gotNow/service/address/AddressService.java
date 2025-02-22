package com.itzkoictu.gotNow.service.address;

import com.itzkoictu.gotNow.dto.response.AddressResponse;
import com.itzkoictu.gotNow.model.Address;
import com.itzkoictu.gotNow.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    public List<Address> createAddress(List<Address> addressList){
        return addressRepository.saveAll(addressList);
    }


    public List<Address> getUserAddresses(Long userId){
        return  addressRepository.findByUserId(userId);

    }

    public Address getAddressById(Long addressId){
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found!"));

    }


    public void deleteAddress(Long addressId){
        addressRepository.findById(addressId).ifPresentOrElse(addressRepository::delete, ()->{
            throw new EntityNotFoundException("Address not found!");
        });
    }


    public Address updateUserAddress(Long id,Address address){
        return addressRepository.findById(id).map(existingAddress -> {
            existingAddress.setCountry(address.getCountry());
            existingAddress.setCity(address.getCity());
            existingAddress.setState(address.getState());
            existingAddress.setStreet(address.getStreet());
            existingAddress.setAddressType(address.getAddressType());

            return addressRepository.save(existingAddress);
        }).orElseThrow(() -> new EntityNotFoundException("Address not found!"));
    }

    public List<AddressResponse> convertToResponse(List<Address> addressList) {
            return addressList.stream().map(this::convertToAddressResponse).toList();
    }

    public AddressResponse convertToAddressResponse(Address address){
        return modelMapper.map(address, AddressResponse.class);
    }

}
