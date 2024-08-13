package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class PostNotFoundException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "게시글을 찾을 수 없습니다.";
    private final ResultCode resultCode;

    public PostNotFoundException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.NOT_FOUND;
    }

    public PostNotFoundException(String message) {
        super(message);
        resultCode = ResultCode.NOT_FOUND;
    }

    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.NOT_FOUND;
    }

    public PostNotFoundException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.NOT_FOUND;
    }
}
