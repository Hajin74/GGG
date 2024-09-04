package org.example.gggauthorization.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

}
