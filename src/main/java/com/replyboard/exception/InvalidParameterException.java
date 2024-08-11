package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InvalidParameterException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "잘못된 파라미터입니다.";
    private final ResultCode resultCode;

    public InvalidParameterException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidParameterException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
