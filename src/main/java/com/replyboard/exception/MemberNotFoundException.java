package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class MemberNotFoundException extends GeneralException{

    private static final String DEFAULT_MESSAGE = "회원을 찾을 수 없습니다.";
    private final ResultCode resultCode;

    public MemberNotFoundException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.NOT_FOUND;
    }

    public MemberNotFoundException(String message) {
        super(message);
        resultCode = ResultCode.NOT_FOUND;
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.NOT_FOUND;
    }

    public MemberNotFoundException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.NOT_FOUND;
    }
}
