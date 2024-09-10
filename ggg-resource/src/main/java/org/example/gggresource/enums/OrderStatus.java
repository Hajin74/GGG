package org.example.gggresource.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {

    ORDERED("주문완료"),
    DEPOSITED("입금완료"),
    DELIVERED("발송완료"),
    TRANSFERRED("송금완료"),
    RECEIVED("수령완료"),
    CANCELED("주문취소");

    private final String korean;

}
