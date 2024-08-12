package com.replyboard.api.handler;

import com.replyboard.api.dto.ApiErrorResponse;
import com.replyboard.constant.ResultCode;
import com.replyboard.exception.GeneralException;
import com.replyboard.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiErrorResponse response = ApiErrorResponse.of(false, ResultCode.BAD_REQUEST);
        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return response;
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiErrorResponse> generalException(GeneralException e) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(false, e.getResultCode(), e.getMessage());

        if (e instanceof InvalidRequestException invalidRequestException) {
            apiErrorResponse.addValidation(invalidRequestException.getValidation());
        }

        return ResponseEntity
                .status(e.getResultCode().getHttpStatus())
                .body(apiErrorResponse);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse exception(Exception e) {
        log.error("{}", e.getClass().getSimpleName(), e);

        return ApiErrorResponse.of(false, ResultCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
