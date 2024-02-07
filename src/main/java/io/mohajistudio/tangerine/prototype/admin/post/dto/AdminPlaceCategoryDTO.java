package io.mohajistudio.tangerine.prototype.admin.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPlaceCategoryDTO {
    @NotNull
    @Schema(description = "PlaceCategory Id", example = "1")
    private Long id;

    @Getter
    @Setter
    @Schema(name = "AdminPlaceCategoryDTO.Details", description = "장소 블럭의 상세를 반환할 때 사용할 DTO")
    public static class Details extends AdminPlaceCategoryDTO {
        @Schema(description = "장소 카테고리명", example = "카페")
        private String name;
    }

    @Getter
    @Schema(name = "AdminPlaceCategoryDTO.Add", description = "장소 블럭을 추가할 때 사용할 DTO")
    public static class Add extends AdminPlaceCategoryDTO {
    }
}
