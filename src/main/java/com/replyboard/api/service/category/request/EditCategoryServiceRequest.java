package com.replyboard.api.service.category.request;

import com.replyboard.exception.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Getter
public class EditCategoryServiceRequest {

    private static final String nameRegExp = "^[a-zA-Z가-힣0-9]*$";
    private final String name;

    @Builder
    public EditCategoryServiceRequest(String name) {
        this.name = name;
    }

    public void validate() {
        if (!StringUtils.hasText(name)) {
            throw new InvalidRequestException("카테고리명은 빈 값일 수 없습니다.");
        }

        if (!Pattern.matches(nameRegExp, name)) {
            throw new InvalidRequestException("카테고리명은 영문자, 숫자, 한글만 가능합니다.");
        }
    }
}
