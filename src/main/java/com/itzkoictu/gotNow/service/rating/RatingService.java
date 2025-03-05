package com.itzkoictu.gotNow.service.rating;


import com.itzkoictu.gotNow.dto.request.RatingRequest;
import com.itzkoictu.gotNow.dto.response.RatingResponse;
import com.itzkoictu.gotNow.enums.OrderStatus;
import com.itzkoictu.gotNow.model.Order;
import com.itzkoictu.gotNow.model.Rating;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.OrderRepository;
import com.itzkoictu.gotNow.repository.ProductRepository;
import com.itzkoictu.gotNow.repository.RatingRepository;
import com.itzkoictu.gotNow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    public Rating createRating(RatingRequest newRating, Long orderId, Long userId, Long productId) {
        Order order= orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found!"));
        if(order.getOrderStatus()== OrderStatus.DELIVERED) {
            if (newRating == null) {
                throw new IllegalArgumentException("Rating request cannot be null");
            }


            Optional.ofNullable(userRepository.existsById(userId))
                    .filter(Boolean::booleanValue)
                    .orElseThrow(() -> new EntityNotFoundException("User ID " + userId + " does not exist"));

            Optional.ofNullable(productRepository.existsById(productId))
                    .filter(Boolean::booleanValue)
                    .orElseThrow(() -> new EntityNotFoundException("Product ID " + productId + " does not exist"));
        }else{
            throw  new IllegalArgumentException("Order have not completed");
        }
        return ratingRepository.save(
                new Rating(null, orderId, productId, userId, newRating.getRating(), newRating.getComment(), LocalDateTime.now())
        );

    }

    public RatingResponse fromRating(Rating rating){
        User user= userRepository.findById(rating.getUserId())
                .orElseThrow(()->new EntityNotFoundException("User not found"));
        RatingResponse response= modelMapper.map(rating, RatingResponse.class);
        response.setUserName(user.getFirstName()+ " " + user.getLastName());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
    public List<RatingResponse> convertToResponses(List<Rating> ratingList){
        return ratingList.stream().map(this::fromRating).toList();
    }

    public List<RatingResponse> getAllRatingsByProductId(Long productId){
        List<Rating> ratingList= ratingRepository.findAllByProductId(productId);


        return  convertToResponses(ratingList);
    }

}
