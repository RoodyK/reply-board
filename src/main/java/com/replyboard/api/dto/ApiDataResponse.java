package com.replyboard.api.dto;

import lombok.Getter;

import static com.replyboard.constant.ResultCode.OK;

@Getter
public class ApiDataResponse<T> extends ApiResponse{

    private final T data;

    private ApiDataResponse(T data) {
        super(true, OK.getCode(), OK.getMessage());
        this.data = data;
    }

    public static <T> ApiDataResponse<T> of(T data) {
        return new ApiDataResponse<>(data);
    }

    public static <T> ApiDataResponse<T> empty() {
        return new ApiDataResponse<>(null);
    }
}
