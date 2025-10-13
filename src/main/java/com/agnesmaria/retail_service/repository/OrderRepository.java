package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();
    private long nextId = 1L;

    public List<Order> findAll() {
        return orders;
    }

    public Order save(Order order) {
        order.setId(nextId++);
        orders.add(order);
        return order;
    }
}
