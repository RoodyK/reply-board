package com.replyboard.api.controller.category.request;

import com.replyboard.api.service.category.request.CreateCategoryServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String name;

    @Builder
    public CreateCategoryRequest(String name) {
        this.name = name;
    }

    public CreateCategoryServiceRequest toServiceRequest() {
        return CreateCategoryServiceRequest.builder()
                .name(name)
                .build();
    }
}
