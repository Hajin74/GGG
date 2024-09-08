package org.example.gggresource.dto;

import org.example.gggresource.enums.OrderType;

public record OrderCreateRequest (
        Long productId,
        Long customerId,
        OrderType orderType,
        int quantity
) {

}
