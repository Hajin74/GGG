package org.example.gggresource.dto;

import org.example.gggresource.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderDetailResponse (
        InvoiceResponse invoiceResponse,
        OrderStatus orderStatus,
        LocalDateTime orderDate

) {
}
