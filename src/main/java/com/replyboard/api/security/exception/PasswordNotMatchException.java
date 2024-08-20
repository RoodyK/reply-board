package com.replyboard.api.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordNotMatchException extends AuthenticationException {

    public PasswordNotMatchException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PasswordNotMatchException(String msg) {
        super(msg);
    }
}
