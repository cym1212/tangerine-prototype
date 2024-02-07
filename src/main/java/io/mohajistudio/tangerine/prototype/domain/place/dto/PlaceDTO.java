package io.mohajistudio.tangerine.prototype.domain.place.dto;

import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
public class PlaceDTO {
    @NotNull
    @Schema(description = "장소명", example = "강남대학교")
    private String name;
    @NotNull
    @Schema(description = "구 주소", example = "경기도 용인시 기흥구 구갈동 111")
    private String address;
    @NotNull
    @Schema(description = "도로명주소", example = "경기 용인시 기흥구 강남로 40")
    private String roadAddress;
    @NotNull
    @Schema(description = "좌표")
    private PointDTO coordinate;

    public void setCoordinate(Point coordinate) {
        double lat = coordinate.getX();
        double lng = coordinate.getY();
        this.coordinate = PointDTO.builder().lat(lat).lng(lng).build();
    }

    @Getter
    @Setter
    public static class Add extends PlaceDTO {
    }

    @Getter
    @Setter
    public static class Details extends PlaceDTO {
        @NotNull
        @Schema(description = "Place Id", example = "1")
        private Long id;
        @Schema(description = "장소 설명", example = "개발팀이 재학중인 학교")
        private String description;
    }
}
