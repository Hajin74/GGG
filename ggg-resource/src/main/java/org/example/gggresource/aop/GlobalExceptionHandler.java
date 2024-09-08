package org.example.gggresource.aop;

import org.example.gggresource.exception.CustomException;
import org.example.gggresource.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getHttpStatus(), errorCode.getErrorMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

}
