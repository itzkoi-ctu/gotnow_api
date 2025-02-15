package com.itzkoictu.gotNow.dto.response;

import com.itzkoictu.gotNow.enums.OrderStatus;
import com.itzkoictu.gotNow.model.OrderItem;
import com.itzkoictu.gotNow.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private List<OrderItemResponse> items;
}
