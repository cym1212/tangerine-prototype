package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface PostMapper {
    PostDTO.Compact toCompactDTO(Post post);

    PostDTO.Details toDetailsDTO(Post post);

    Post toEntity(PostDTO.Details postDetailsDTO);


    Post toEntity(PostDTO.Add postAddDTO);

    @Mapping(source = ".", target = "coordinate", qualifiedByName = "setDetailsDTOCoordinate")
    Place toEntity(PlaceDTO.Details placeDetailsDTO);

    @Named("setDetailsDTOCoordinate")
    default Point setDetailsDTOCoordinates(PlaceDTO.Details placeDetailsDTO) {
        double lat = placeDetailsDTO.getCoordinates().getLat();
        double lng = placeDetailsDTO.getCoordinates().getLng();
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }
}
