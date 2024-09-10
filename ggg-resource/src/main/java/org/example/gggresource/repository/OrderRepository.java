package org.example.gggresource.repository;

import org.example.gggresource.domain.entity.Order;
import org.example.gggresource.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
            "WHERE o.customerId = :customerId " +
            "AND (:startOfDay IS NULL OR (o.orderDate BETWEEN :startOfDay AND :endOfDay)) " +
            "AND (:invoiceType IS NULL OR o.orderType = :invoiceType)")
    Page<Order> searchOrders(Long customerId, LocalDateTime startOfDay, LocalDateTime endOfDay, OrderType invoiceType, Pageable pageable);
}
