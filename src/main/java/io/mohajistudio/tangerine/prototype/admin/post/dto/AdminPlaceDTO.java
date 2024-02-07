package io.mohajistudio.tangerine.prototype.admin.post.dto;

import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
public class AdminPlaceDTO {
    @NotNull
    @Schema(description = "Place Id", example = "1")
    private Long id;

    @Getter
    @Setter
    @Schema(name = "AdminPlaceDTO.Details", description = "장소 블럭을 추가할 때 사용할 DTO")
    public static class Details extends AdminPlaceDTO {
        @Schema(description = "장소명", example = "강남대학교")
        private String name;
        @Schema(description = "좌표")
        private PointDTO coordinates;
        @Schema(description = "썸네일", example = "https://thumbnail.com")
        private String thumbnail;
        @Schema(description = "구 주소(광역시, 도)", example = "경기도")
        private String addressProvince;
        @Schema(description = "구 주소(시/군/구)", example = "용인시")
        private String addressCity;
        @Schema(description = "구 주소(읍/면/동)", example = "기흥구")
        private String addressDistrict;
        @Schema(description = "구 주소(이하)", example = "구갈동 111")
        private String addressDetail;
        @Schema(description = "도로명주소", example = "경기 용인시 기흥구 강남로 40")
        private String roadAddress;
        @Schema(description = "장소 설명", example = "개발팀이 재학중인 학교")
        private String description;

        public void setCoordinates(Point coordinates) {
            double lat = coordinates.getX();
            double lng = coordinates.getY();
            this.coordinates = PointDTO.builder().lat(lat).lng(lng).build();
        }
    }

    @Getter
    @Schema(name = "AdminPlaceDTO.Add", description = "장소 블럭을 추가할 때 사용할 DTO")
    public static class Add extends AdminPlaceDTO {
    }
}
