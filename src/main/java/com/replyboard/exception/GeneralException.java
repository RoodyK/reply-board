package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ResultCode resultCode;

    public GeneralException() {
        resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }

    public GeneralException(String message) {
        super(message);
        resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }

    public GeneralException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }
}
