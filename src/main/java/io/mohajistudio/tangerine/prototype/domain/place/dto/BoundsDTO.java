package io.mohajistudio.tangerine.prototype.domain.place.dto;

import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoundsDTO {
    PointDTO northEast;
    PointDTO southWest;
}
