package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.PaymentRequest;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.OrderResponse;
import com.itzkoictu.gotNow.model.Order;
import com.itzkoictu.gotNow.service.order.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/user/{userId}/place-order")
    public ResponseEntity<ApiResponse> placeOrder(@PathVariable Long userId) {
        Order order = orderService.placeOrder(userId);
        OrderResponse orderResponse = orderService.convertToOrderResponse(order);
        return ResponseEntity.accepted().body(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "Order placed successfully!", orderResponse));

    }

    @GetMapping("/user/{userId}/order")
    private ResponseEntity<ApiResponse> getUserOrder(@PathVariable Long userId) {
        List<OrderResponse> orderList = orderService.getUserOrders(userId);
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "all orders of user " + userId, orderList));


    }


    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest request) throws StripeException {
        String clientSecret = orderService.createPaymentIntent(request);
        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
    }

}
