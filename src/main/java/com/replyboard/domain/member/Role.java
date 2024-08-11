package com.replyboard.domain.member;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_ADMIN("최고 관리자"),
    ROLE_MANAGER("관리자"),
    ROLE_USER("사용자"),
    ;

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
