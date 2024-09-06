package org.example.gggauthorization.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    ACCESS_TOKEN_NOT_EXISTED(HttpStatus.BAD_REQUEST, "Access Token 이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_EXISTED(HttpStatus.BAD_REQUEST, "Refresh Token 이 존재하지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰 타입입니다."),
    TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

}
