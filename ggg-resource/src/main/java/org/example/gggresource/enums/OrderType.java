package org.example.gggresource.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderType {

    SELL("판매"),
    PURCHASE("매입");

    private final String korean;

}
