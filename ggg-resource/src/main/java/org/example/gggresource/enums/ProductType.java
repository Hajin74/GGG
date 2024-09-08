package org.example.gggresource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {

    SELL("판매"),
    BUY("매입");

    private final String korean;

}
