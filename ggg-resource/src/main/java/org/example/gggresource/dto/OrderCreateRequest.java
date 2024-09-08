package org.example.gggresource.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderCreateRequest (
        @NotNull(message = "상품 ID는 필수 입력 값입니다.")
        @Positive
        Long productId,
        @NotNull(message = "소비자 ID는 필수 입력 값입니다.")
        @Positive
        Long customerId,
        @NotNull(message = "상품 수량은 필수 입력 값입니다.")
        @Positive
        int quantity
) {

}
