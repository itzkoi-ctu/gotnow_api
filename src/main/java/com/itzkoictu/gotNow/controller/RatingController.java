package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.RatingRequest;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.RatingResponse;
import com.itzkoictu.gotNow.model.Rating;
import com.itzkoictu.gotNow.service.rating.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{orderId}/add/{userId}/rating/{productId}")
    public ResponseEntity<ApiResponse> createRating(@RequestBody RatingRequest request, @PathVariable Long orderId, @PathVariable Long userId, @PathVariable Long productId){
            System.out.println("request"+ request);
            Rating rating= ratingService.createRating(request,orderId,userId, productId);
            return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "Post rating successfully!", rating));
    }

    @GetMapping("/all/{productId}/rating")
    public ResponseEntity<ApiResponse> getAllByProductId(@PathVariable Long productId){

        List<RatingResponse> rating= ratingService.getAllRatingsByProductId( productId);
        return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "Post rating successfully!", rating));
    }


}
