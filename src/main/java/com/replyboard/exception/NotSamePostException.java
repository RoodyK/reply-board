package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class NotSamePostException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "댓글이 작성된 게시글과 일치하지 않습니다.";
    private final ResultCode resultCode;

    public NotSamePostException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotSamePostException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotSamePostException(String field, String message) {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public NotSamePostException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
