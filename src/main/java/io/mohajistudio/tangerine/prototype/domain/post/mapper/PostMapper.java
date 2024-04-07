package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import io.mohajistudio.tangerine.prototype.global.common.PointDTO;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper
public interface PostMapper {
    String regex = "^(?<province>\\S+)\\s+(?<city>\\S+)\\s+(?<district>\\S+)\\s*(?<detail>.+)?$";

    Pattern pattern = Pattern.compile(regex);

    PostDTO.Compact toCompactDTO(Post post);

    PostDTO.Details toDetailsDTO(Post post);

    Post toEntity(PostDTO.Details postDetailsDTO);

    Post toEntity(PostDTO.Add postAddDTO);

    @Mapping(target = "representativePlaceBlockImageOrderNumber", ignore = true)
    PlaceBlockDTO.Details toDTO(PlaceBlock placeBlock);

    @Mapping(source = "coordinate", target = "coordinate", qualifiedByName = "setCoordinate")
    Place toPlaceDetailsDTO(PlaceDTO.Details placeDetailsDTO);

    @Mapping(source = "coordinate", target = "coordinate", qualifiedByName = "setDTOCoordinate")
    PlaceDTO.Details toPlaceDetailsDTO(Place place);

    @Named("convertToProvince") // 2
    default String convertToProvince(String address) {
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            return matcher.group("province");
        }
        throw new BusinessException("지원하지 않는 주소 형식입니다, address = " + address, ErrorCode.INVALID_INPUT_VALUE);
    }

    @Named("convertToCity") // 2
    default String convertToCity(String address) {
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            return matcher.group("city");
        }
        throw new BusinessException("지원하지 않는 주소 형식입니다, address = " + address, ErrorCode.INVALID_INPUT_VALUE);
    }

    @Named("convertToDistrict") // 2
    default String convertToDistrict(String address) {
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            return matcher.group("district");
        }
        throw new BusinessException("지원하지 않는 주소 형식입니다, address = " + address, ErrorCode.INVALID_INPUT_VALUE);
    }

    @Named("convertToDetail") // 2
    default String convertToDetail(String address) {
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            String detail = matcher.group("detail");
            if (detail == null) {
                return "";
            }
            return detail;
        }
        throw new BusinessException("지원하지 않는 주소 형식입니다, address = " + address, ErrorCode.INVALID_INPUT_VALUE);
    }

    @Named("setCoordinate")
    default Point setPlaceAddDTOCoordinate(PointDTO coordinate) {
        double lat = coordinate.getLat();
        double lng = coordinate.getLng();
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }

    @Named("setDTOCoordinate")
    default PointDTO setCoordinate(Point coordinate) {
        double lat = coordinate.getX();
        double lng = coordinate.getY();
        return PointDTO.builder().lat(lat).lng(lng).build();
    }
}
