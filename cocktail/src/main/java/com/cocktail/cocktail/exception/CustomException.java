package com.cocktail.cocktail.exception;

import com.cocktail.cocktail.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public CustomException(String message, ErrorCode errorCode, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
