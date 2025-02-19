package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.model.Cart;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.service.cart.CartItemService;
import com.itzkoictu.gotNow.service.cart.CartService;
import com.itzkoictu.gotNow.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final CartItemService cartItemService;
    private final UserService userService;
    private final CartService cartService;

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId,
                                                     @RequestParam int quantity){
//            User user= userService.getAuthenticatedUser();
//            Cart cart= cartService.initializeNewCartForUser(user);
            cartItemService.addItemToCart(1L, productId, quantity);

            return ResponseEntity.accepted().body(new ApiResponse(HttpStatus.ACCEPTED.value(), "Item added successfully", null));
    }
    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId){
        cartItemService.removeItemFromCart(cartId,itemId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(),"Item remove successfully", null));
    }

    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestParam int quantity){
        cartItemService.updateItemQuantity(cartId,itemId,quantity);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Item updated successfullt"));

    }

}
