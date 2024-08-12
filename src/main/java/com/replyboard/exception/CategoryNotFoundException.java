package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class CategoryNotFoundException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "카테고리를 찾을 수 없습니다.";
    private final ResultCode resultCode;

    public CategoryNotFoundException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CategoryNotFoundException(String message) {
        super(message);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.NOT_FOUND;
    }

    public CategoryNotFoundException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.NOT_FOUND;
    }
}
