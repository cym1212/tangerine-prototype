package io.mohajistudio.tangerine.prototype.global.enums;

import lombok.Getter;

@Getter
public enum ResolutionStatus {
    PENDING("대기중"),
    RESOLVED("처리됨"),
    REJECTED("거부됨");

    private final String title;

    ResolutionStatus(String title) {
        this.title = title;
    }
}
