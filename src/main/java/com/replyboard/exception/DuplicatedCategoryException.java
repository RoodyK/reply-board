package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class DuplicatedCategoryException extends GeneralException {

    private static final String DEFAULT_MESSAGE = "중복된 카테고리명이 존재합니다.";
    private final ResultCode resultCode;

    public DuplicatedCategoryException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public DuplicatedCategoryException(String message) {
        super(message);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public DuplicatedCategoryException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.BAD_REQUEST;
    }

    public DuplicatedCategoryException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.BAD_REQUEST;
    }
}
