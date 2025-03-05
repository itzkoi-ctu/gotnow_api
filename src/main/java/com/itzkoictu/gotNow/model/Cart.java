package com.itzkoictu.gotNow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal totalAmount;

    @OneToOne
    @JoinColumn(name= "user_id")
    private User user;

    @OneToMany(mappedBy = "cart")
    Set<CartItem> items= new HashSet<>();

    public void removeItem(CartItem cartItem) {
        this.items.remove(cartItem);
        cartItem.setCart(null);

        updateTotalAmount();
    }

    private void updateTotalAmount() {
        this.totalAmount= items.stream().map(item -> {
            BigDecimal unitPrice= item.getUnitPrice();
            if(unitPrice==null){
                return BigDecimal.ZERO;
            }
            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void addItem(CartItem cartItem) {
        this.items.add(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();
    }

    public void clearCart() {
        this.items.clear();
        updateTotalAmount();
    }
}
