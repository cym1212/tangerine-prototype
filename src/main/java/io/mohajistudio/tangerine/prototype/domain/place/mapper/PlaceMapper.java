package io.mohajistudio.tangerine.prototype.domain.place.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.dto.PlaceDTO;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.place.dto.PlaceKakaoSearchApiDTO;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper
public interface PlaceMapper {
    @Mapping(source = ".", target = "address", qualifiedByName = "setAddress")
    PlaceDTO.Details toDetailsDTO(Place place);

    @Named("setAddress")
    default String setAddress(Place place) {
        return place.getAddress();
    }
}
