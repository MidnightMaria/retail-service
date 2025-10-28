package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.OrderItem;
import com.agnesmaria.retail_service.model.Order;
import com.agnesmaria.retail_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
}
