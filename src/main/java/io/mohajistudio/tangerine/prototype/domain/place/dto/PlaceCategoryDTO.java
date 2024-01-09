package io.mohajistudio.tangerine.prototype.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCategoryDTO {
    @Schema(description = "PlaceCategory Id", example = "1")
    private Long id;

    @Schema(description = "장소 카테고리 이름", example = "강남대학교")
    private String name;
}
