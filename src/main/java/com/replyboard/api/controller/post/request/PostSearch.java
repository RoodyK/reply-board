package com.replyboard.api.controller.post.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Setter
@Getter
@NoArgsConstructor
public class PostSearch {

    private String searchValue;

    @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
    private int page;

    @Builder
    public PostSearch(String searchValue, int page) {
        this.searchValue = searchValue;
        this.page = page;
    }

    public long getOffset(int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        return (long) (page - 1) * pageSize;
    }

    public Integer getPage() {
        return page <= 0 ? 1 : page;
    }

    public Pageable getPageable(Integer pageSize) {
        if (page <= 0) {
            page = 1;
        }
        return PageRequest.of(page - 1, pageSize);
    }
}
