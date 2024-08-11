package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InvalidRequestException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "잘못된 요청입니다.";
    private final ResultCode resultCode;
    private final Map<String, Object> validation = new HashMap<>();

    public InvalidRequestException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidRequestException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidRequestException(String field, String message) {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
        validation.put(field, message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
