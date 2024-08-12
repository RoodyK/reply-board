package com.replyboard.api.controller.category.request;

import com.replyboard.api.service.category.request.CreateCategoryServiceRequest;
import com.replyboard.api.service.category.request.EditCategoryServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EditCategoryRequest {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String name;

    @Builder
    public EditCategoryRequest(String name) {
        this.name = name;
    }

    public EditCategoryServiceRequest toServiceRequest() {
        return EditCategoryServiceRequest.builder()
                .name(name)
                .build();
    }
}
