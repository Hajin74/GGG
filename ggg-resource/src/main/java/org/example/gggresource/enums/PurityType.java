package org.example.gggresource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PurityType {

    GOLD_999("99.9"),
    GOLD_9999("99.99");

    private final String korean;

}
