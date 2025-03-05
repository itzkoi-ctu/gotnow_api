package com.itzkoictu.gotNow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;


@Setter
@NoArgsConstructor
@AllArgsConstructor

@Getter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private BigDecimal price;


    @ManyToOne
    @JoinColumn(name = "order_id")
        private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // ✅ Khi xóa Product, xóa luôn OrderItem liên quan

    private Product product;

    public OrderItem( Order order,Product product, BigDecimal price, int quantity) {
        this.order = order;

        this.product = product;
        this.price = price;
        this.quantity = quantity;


    }


}
