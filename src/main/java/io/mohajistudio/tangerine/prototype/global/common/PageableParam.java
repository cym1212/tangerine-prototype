package io.mohajistudio.tangerine.prototype.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Schema(description = "Paging Request Parameter")
public class PageableParam {

    @Setter
    @Schema(description = "페이지 크기를 나눌 사이즈 개수", example = "10")
    int size = 10;
    @Schema(description = "사이즈 크기로 나눈 페이지 번호로 0부터 시작", example = "1")
    int page = 0;

    public void setPage(int page) {
        this.page = page - 1;
    }
}
