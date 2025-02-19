package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.CartResponse;
import com.itzkoictu.gotNow.model.Cart;
import com.itzkoictu.gotNow.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final CartService cartService;

    @GetMapping("/user/{userId}/cart")
    public ResponseEntity<ApiResponse> getUserCart(@PathVariable Long userId){
        Cart cart= cartService.getCartByUserId(userId);
        CartResponse cartResponse= cartService.convertToCartResponse(cart);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", cartResponse));
    }
    @DeleteMapping("/cart/{cartId}/clear")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId){
        cartService.clearCart(cartId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart cleared successfully"));

    }
}
