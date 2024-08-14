package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class CommentNotFoundException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "댓글을 찾을 수 없습니다.";
    private final ResultCode resultCode;

    public CommentNotFoundException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CommentNotFoundException(String message) {
        super(message);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CommentNotFoundException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.NOT_FOUND;
    }
}
