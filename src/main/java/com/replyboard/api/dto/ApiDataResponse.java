package com.replyboard.api.dto;

import com.replyboard.constant.ResultCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.replyboard.constant.ResultCode.*;

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
