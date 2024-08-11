package com.replyboard.exception;

import com.replyboard.constant.ResultCode;
import lombok.Getter;

@Getter
public class DuplicatedMemberException extends GeneralException{

    private static final String DEFAULT_MESSAGE = "중복된 회원이 존재합니다.";
    private final ResultCode resultCode;

    public DuplicatedMemberException() {
        super(DEFAULT_MESSAGE);
        resultCode = ResultCode.DUPLICATED_MEMBER;
    }

    public DuplicatedMemberException(String message) {
        super(message);
        resultCode = ResultCode.DUPLICATED_MEMBER;
    }

    public DuplicatedMemberException(String message, Throwable cause) {
        super(message, cause);
        resultCode = ResultCode.DUPLICATED_MEMBER;
    }

    public DuplicatedMemberException(Throwable cause) {
        super(cause);
        resultCode = ResultCode.DUPLICATED_MEMBER;
    }
}
