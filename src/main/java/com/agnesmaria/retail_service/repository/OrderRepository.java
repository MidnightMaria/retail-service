package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.Order;
import com.agnesmaria.retail_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomer(Customer customer);
    List<Order> findByStatus(String status);
}
