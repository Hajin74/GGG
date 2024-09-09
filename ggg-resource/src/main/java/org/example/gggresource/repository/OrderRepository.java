package org.example.gggresource.repository;

import org.example.gggresource.domain.entity.Order;
import org.example.gggresource.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
            "WHERE o.customerId = :customerId " +
            "AND (:date IS NULL OR o.orderDate = :date) " +
            "AND (:invoiceType IS NULL OR o.orderType = :invoiceType)")
    Page<Order> searchOrders(Long customerId, LocalDate date, OrderType invoiceType, Pageable pageable);
}
