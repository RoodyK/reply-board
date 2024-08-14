package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class NotMatchPasswordException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "비밀번호가 일치하지 않습니다.";
    private final ResultCode resultCode;

    public NotMatchPasswordException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotMatchPasswordException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotMatchPasswordException(String field, String message) {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotMatchPasswordException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
