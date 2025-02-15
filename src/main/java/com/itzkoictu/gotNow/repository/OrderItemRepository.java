package com.itzkoictu.gotNow.repository;

import com.itzkoictu.gotNow.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findProductById(Long productId);
}
