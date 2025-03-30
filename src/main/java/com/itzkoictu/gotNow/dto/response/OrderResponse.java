package com.itzkoictu.gotNow.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.Set;
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime deliveredDay;
    private Set<OrderItemResponse> items;
}
