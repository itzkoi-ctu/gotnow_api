package com.itzkoictu.gotNow.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class CartResponse {
    private Long cartId;
    private Set<CartItemResponse> items;
    private BigDecimal totalAmount;
}
