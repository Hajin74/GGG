package org.example.gggauthorization.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    TOKEN_NOT_EXISTED(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
    TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

}
