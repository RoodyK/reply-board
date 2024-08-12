package com.replyboard.api.service.category.response;

import com.replyboard.domain.category.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponse {

    private Long id;
    private String name;

    @Builder
    private CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
