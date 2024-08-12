package com.replyboard.api.service.category.request;

import com.replyboard.domain.category.Category;
import com.replyboard.domain.member.Member;
import com.replyboard.exception.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class CreateCategoryServiceRequest {

    private static final String nameRegExp = "^[a-zA-Z가-힣0-9]*$";
    private String name;

    @Builder
    public CreateCategoryServiceRequest(String name) {
        this.name = name;
    }

    public Category toEntity(Member member) {
        Category category = Category.builder()
                .name(name)
                .build();

        category.addMember(member);

        return category;
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
