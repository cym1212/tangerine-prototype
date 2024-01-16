package io.mohajistudio.tangerine.prototype.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextBlockDTO {
    @NotNull
    @Schema(description = "순서 번호", example = "1")
    private short orderNumber;

    @NotNull
    @Schema(description = "내용", example = "첫 번째 텍스트 블럭에 들어갈 내용")
    private String content;

    @Getter
    @Setter
    @Schema(name = "TextBlockDTO.Details", description = "텍스트 블럭의 상세를 반환할 때 사용할 DTO")
    public static class Details extends TextBlockDTO {
        @Schema(description = "PlaceBlock Id", example = "1")
        private Long id;
    }

    @Getter
    @Setter
    @Schema(name = "TextBlockDTO.Add", description = "텍스트 블럭을 추가할 때 사용할 DTO")
    public static class Add extends TextBlockDTO {
    }
}
