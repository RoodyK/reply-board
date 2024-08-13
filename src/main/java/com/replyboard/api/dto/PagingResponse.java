package com.replyboard.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.lang.reflect.Constructor;
import java.util.List;

@Getter
public class PagingResponse<T> {
    private final int page;
    private final int pageSize;
    private final long totalCount;
    private final List<T> items;

    public PagingResponse(Page<?> page, Class<T> clazz) {
        this.page = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalCount = page.getTotalElements();
        this.items = page.getContent().stream()
                .map(item -> {
                    try {
                        // 리플렉션 사용
                        // 1. clazz의 생성자 중 매개변수 타입이 content.getClass()와 일치하는 생성자를 찾는다.
                        // 2. 일치하는 생성자를 찾으면 생성자를 호출해서 인스턴스를 생성한다.
                        Constructor<T> constructor = clazz.getDeclaredConstructor(item.getClass());
                        return constructor.newInstance(item);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public PagingResponse(int page, int pageSize, long totalCount, List<T> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.items = items;
    }
}
