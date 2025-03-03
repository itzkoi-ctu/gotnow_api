package com.itzkoictu.gotNow.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.Set;
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Set<OrderItemResponse> items;
}
