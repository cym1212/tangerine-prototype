package io.mohajistudio.tangerine.prototype.domain.post.dto;

import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import io.mohajistudio.tangerine.prototype.global.enums.PlaceProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDTO {
    @Schema(description = "장소명", example = "강남대학교")
    private String name;
    @Schema(description = "구 주소", example = "경기 용인시 기흥구 구갈동 111")
    private String address;
    @Schema(description = "도로명주소", example = "경기 용인시 기흥구 강남로 40")
    private String roadAddress;
    @Schema(description = "장소 설명", example = "개발팀이 재학중인 학교")
    private String description;
    @Schema(description = "장소 링크", example = "https://kangnam.ac.kr")
    private String link;
    @Schema(description = "장소 정보 제공 출처", example = "KAKAO")
    private PlaceProvider placeSearchProvider;
    @Schema(description = "장소 정보 제공 출처의 Id", example = "11045491")
    private Long providerId;
    @Schema(description = "좌표")
    private PointDTO coordinate;

    @Getter
    @Setter
    @Schema(name = "PlaceDTO.Details", description = "장소 상세를 반환할 때 사용할 DTO")
    public static class Details extends PlaceDTO {
        @Schema(description = "Place Id", example = "1")
        private Long id;
    }
}
