package org.example.gggresource.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gggresource.enums.OrderStatus;
import org.example.gggresource.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product orderProduct;

    // 인증 서버에서 사용자 ID
    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String deliverInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Builder
    public Order(String orderNumber, Product orderProduct, Long customerId, BigDecimal orderPrice, int quantity, BigDecimal totalPrice, LocalDateTime orderDate, String deliverInfo, OrderStatus orderStatus, OrderType orderType) {
        this.orderNumber = orderNumber;
        this.orderProduct = orderProduct;
        this.customerId = customerId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliverInfo = deliverInfo;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
    }

    public void completeDeposit() {
        this.orderStatus = OrderStatus.PAID;
    }

}
