package com.itzkoictu.gotNow.service.cart;

import com.itzkoictu.gotNow.dto.response.CartResponse;
import com.itzkoictu.gotNow.model.Cart;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.CartItemRepository;
import com.itzkoictu.gotNow.repository.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    public Cart getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        return cart;
    }


    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = getCart(cartId);

        cartItemRepository.deleteAllByCartId(cartId);
        cart.clearCart();
        cartRepository.deleteById(cartId);
    }

    public Cart initializeNewCartForUser(User user) {
        return Optional.ofNullable(getCartByUserId(user.getId()))
                .orElseGet(() -> {
                    System.out.println("userId is added cartItem: "+ user.getId());
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public BigDecimal getTotalPrice(Long cartId) {
        Cart cart = getCart(cartId);
        return cart.getTotalAmount();
    }

    public CartResponse convertToCartResponse(Cart cart){
        CartResponse cartResponse= modelMapper.map(cart, CartResponse.class);
        return  cartResponse;
    }
}
