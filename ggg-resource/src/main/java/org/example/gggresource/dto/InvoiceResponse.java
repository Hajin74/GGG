package org.example.gggresource.dto;

import java.math.BigDecimal;

public record InvoiceResponse (
    String orderNumber,
    BigDecimal orderPrice,
    int quantity,
    BigDecimal totalPrice,
    String deliverInfo
) {
}
