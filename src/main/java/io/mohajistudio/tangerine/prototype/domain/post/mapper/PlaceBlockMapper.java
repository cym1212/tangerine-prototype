package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceDTO;
import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface PlaceBlockMapper {
    PlaceBlockDTO.Details toDetailsDTO(PlaceBlock placeBlock);

    @Mapping(source = "coordinate", target = "coordinate", qualifiedByName = "setDTOCoordinate")
    PlaceDTO.Details toPlaceDetailsDTO(Place place);

    @Named("setDTOCoordinate")
    default PointDTO setCoordinate(Point coordinate) {
        double lat = coordinate.getX();
        double lng = coordinate.getY();
        return PointDTO.builder().lat(lat).lng(lng).build();
    }
}
