package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class CanNotAnotherReplyException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "답글에 댓글을 달 수 없습니다.";
    private final ResultCode resultCode;

    public CanNotAnotherReplyException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public CanNotAnotherReplyException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public CanNotAnotherReplyException(String field, String message) {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public CanNotAnotherReplyException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
