package io.mohajistudio.tangerine.prototype.infra.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceKakaoSearchApiDTO {
    @NotNull
    @Schema(description = "PlaceKakaoSearchApi Id")
    private Long id;
    @NotNull
    @Schema(description = "장소명", example = "강남대학교")
    private String placeName;
    @Schema(description = "카테고리명", example = "교육,학문 > 학교 > 대학교")
    private String categoryName;
    @Schema(description = "카테고리 그룹 코드", example = "SC4")
    private String categoryGroupCode;
    @Schema(description = "카테고리 그룹명", example = "학교")
    private String categoryGroupName;
    @Schema(description = "전화번호", example = "031-280-3500")
    private String phone;
    @NotNull
    @Schema(description = "구 주소", example = "경기 용인시 기흥구 구갈동 111")
    private String addressName;
    @NotNull
    @Schema(description = "구 주소", example = "경기 용인시 기흥구 강남로 40")
    private String roadAddressName;
    @NotNull
    @Schema(description = "좌표 x", example = "127.13315421201591")
    private String x;
    @NotNull
    @Schema(description = "좌표 y", example = "37.27603234338971")
    private String y;
    @Schema(description = "링크", example = "http://place.map.kakao.com/11045491")
    private String placeUrl;
}
