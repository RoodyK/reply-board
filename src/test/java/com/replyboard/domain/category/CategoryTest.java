package com.replyboard.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @DisplayName("카테고리명을 변경한다")
    @Test
    void edit() {
        // given
        Category category = Category.builder().name("요리").build();

        // when
        category.edit("기타");

        // then
        assertThat(category.getName()).isEqualTo("기타");
    }

}