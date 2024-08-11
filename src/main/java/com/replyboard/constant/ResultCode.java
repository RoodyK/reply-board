package com.replyboard.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Getter
public enum ResultCode {

    OK(200, HttpStatus.OK, "OK"),

    BAD_REQUEST(1000, HttpStatus.BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED(1100, HttpStatus.UNAUTHORIZED, "UnAuthorized"),
    FORBIDDEN(1200, HttpStatus.FORBIDDEN, "Forbidden"),
    NOT_FOUND(1300, HttpStatus.NOT_FOUND, "Not Found"),

    DUPLICATED_MEMBER(5000, HttpStatus.CONFLICT, "Duplicated Member"),

    INTERNAL_SERVER_ERROR(10000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    ResultCode(Integer code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getMessage(Throwable e) {
        return getMessage(this.getMessage() + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(StringUtils::hasText)
                .orElse(this.getMessage());
    }
}
