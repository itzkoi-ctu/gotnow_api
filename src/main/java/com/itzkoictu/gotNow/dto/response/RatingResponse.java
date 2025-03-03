package com.itzkoictu.gotNow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private String id;
    private Long orderId;
    private Long productId;
    private double rating;
    private String comment;
    private String userName; // Thêm tên user
    private LocalDateTime createdAt;
}
