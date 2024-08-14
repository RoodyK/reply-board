package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class NotOwnPostException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "자신의 게시글이 아닙니다.";
    private final ResultCode resultCode;

    public NotOwnPostException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotOwnPostException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotOwnPostException(String field, String message) {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotOwnPostException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
