package org.example.gggresource.dto;

import org.example.gggresource.enums.OrderStatus;

public record OrderStatusUpdateResponse(
        String orderNumber,
        OrderStatus orderStatus
) {
}
