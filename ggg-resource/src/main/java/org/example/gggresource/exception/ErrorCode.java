package org.example.gggresource.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    AUTHORIZATION_FAILED(HttpStatus.BAD_REQUEST, "사용자 검증에 실패했습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    PRODUCT_ONLY_FOR_SELL(HttpStatus.NOT_FOUND, "해당 상품은 판매용입니다."),
    PRODUCT_ONLY_FOR_PURCHASE(HttpStatus.NOT_FOUND, "해당 상품은 매입용입니다."),
    INVALID_ORDER_STATUS(HttpStatus.NOT_FOUND, "주문 상태가 유효하지 않습니다."),
    ORDER_ALREADY_DELIVERED(HttpStatus.CONFLICT, "이미 발송 완료되어 주문을 취소할 수 없습니다."),
    ORDER_ALREADY_RECEIVED(HttpStatus.CONFLICT, "이미 수령 완료되어 주문을 취소할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

}
