package com.replyboard.api.dto;

import com.replyboard.constant.ResultCode;

import java.util.HashMap;
import java.util.Map;

public class ApiErrorResponse extends ApiResponse{

    private Map<String, Object> validation;

    private ApiErrorResponse(Boolean result, Integer code, String message) {
        super(result, code, message);
    }

    public static ApiErrorResponse of (Boolean result, Integer code, String message) {
        return new ApiErrorResponse(result, code, message);
    }

    public static ApiErrorResponse of (Boolean result, ResultCode resultCode) {
        return new ApiErrorResponse(result, resultCode.getCode(), resultCode.getMessage());
    }

    public static ApiErrorResponse of (Boolean result, ResultCode resultCode, Exception e) {
        return new ApiErrorResponse(result, resultCode.getCode(), resultCode.getMessage(e));
    }

    public static ApiErrorResponse of(Boolean result, ResultCode resultCode, String message) {
        return new ApiErrorResponse(result, resultCode.getCode(), resultCode.getMessage(message));
    }

    public Map<String, Object> getValidation() {
        if (validation == null) {
            validation = new HashMap<>();
        }

        return validation;
    }

    public void addValidation(String field, String defaultMessage) {
        if (validation == null) {
            validation = new HashMap<>();
        }

        validation.put(field, defaultMessage);
    }

    public void addValidation(Map<String, Object> validation) {
        this.validation = new HashMap<>(validation);
    }
}
