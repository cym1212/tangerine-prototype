package io.mohajistudio.tangerine.prototype.domain.post.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PlaceBlockDTO {
    @NotNull
    @PastOrPresent
    @Schema(description = "방문 시작 날짜")
    private LocalDate visitStartDate;

    @NotNull
    @PastOrPresent
    @Schema(description = "방문 도착 날짜")
    private LocalDate visitEndDate;

    @NotNull
    @Schema(description = "순서 번호", example = "2")
    private short orderNumber;

    @NotNull
    @Schema(description = "내용", example = "첫 번째 텍스트 블럭에 들어갈 내용")
    private String content;

    @NotNull
    @Schema(description = "별점", example = "장소에 대한 별점")
    private Short rating;

    @Getter
    @Setter
    @Schema(name = "PlaceBlockDTO.Details", description = "장소 블럭의 상세를 반환할 때 사용할 DTO")
    public static class Details extends PlaceBlockDTO {
        @NotNull
        @Schema(description = "PlaceBlock Id", example = "1")
        private Long id;
        @Valid
        @Schema(description = "카테고리")
        private PlaceCategoryDTO.Details placeCategory;
        @Valid
        @ArraySchema(arraySchema = @Schema(description = "장소 블럭 이미지"))
        private List<PlaceBlockImageDTO.Details> placeBlockImages;
        @Valid
        @Schema(description = "장소")
        private PlaceDTO.Details place;
        @Schema(description = "Representative PlaceBlockImage Id", example = "1")
        private Long representativePlaceBlockImageId;
        @Min(1)
        @Schema(description = "대표 이미지 순서 번호", example = "1")
        private Short representativePlaceBlockImageOrderNumber;
    }

    @Getter
    @Setter
    @Schema(name = "PlaceBlockDTO.Add", description = "장소 블럭을 추가할 때 사용할 DTO")
    public static class Add extends PlaceBlockDTO {
        @NotNull
        @Schema(description = "대표 이미지 순서 번호", example = "1")
        private Short representativePlaceBlockImageOrderNumber;
        @Valid
        @NotNull
        @Schema(description = "카테고리")
        private PlaceCategoryDTO.Add placeCategory;
        @Valid
        @NotNull
        @ArraySchema(arraySchema = @Schema(description = "장소 블럭 이미지"))
        private List<PlaceBlockImageDTO.Add> placeBlockImages;
        @Valid
        @NotNull
        @Schema(description = "장소")
        private PlaceDTO.Add place;
    }
}
