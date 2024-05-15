package io.mohajistudio.tangerine.prototype.domain.place.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.place.dto.KakaoPlaceDTO;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper
public interface PlaceMapper {
    String regexp = "^(?<province>\\S+)\\s+(?<city>\\S+)\\s+(?<district>\\S+)\\s*(?<detail>.+)?$";
    Pattern pattern = Pattern.compile(regexp);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "providerId")
    @Mapping(source = "placeName", target = "name")
    @Mapping(source = ".", target = "coordinate", qualifiedByName = "setCoordinate")
    @Mapping(source = "addressName", target = "addressProvince", qualifiedByName = "convertToProvince")
    @Mapping(source = "addressName", target = "addressCity", qualifiedByName = "convertToCity")
    @Mapping(source = "addressName", target = "addressDistrict", qualifiedByName = "convertToDistrict")
    @Mapping(source = "addressName", target = "addressDetail", qualifiedByName = "convertToDetail")
    @Mapping(source = "roadAddressName", target = "roadAddress")
    @Mapping(source = "categoryName", target = "description")
    @Mapping(source = "placeUrl", target = "link")
    Place toEntity(KakaoPlaceDTO kakaoPlace);

    @Named("setCoordinate")
    default Point setCoordinate(KakaoPlaceDTO kakaoPlaceDTO) {
        double lat = Double.parseDouble(kakaoPlaceDTO.getY());
        double lng = Double.parseDouble(kakaoPlaceDTO.getX());
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }

    @Named("convertToProvince") // 2
    default String convertToProvince(String address) {
        Matcher matcher = pattern.matcher(address);
        if (matcher.matches()) {
            String province =  matcher.group("province");
            Map<String, String> provinceMap = new HashMap<>();
            provinceMap.put("서울", "서울특별시");
            provinceMap.put("부산", "부산광역시");
            provinceMap.put("대구", "대구광역시");
            provinceMap.put("인천", "인천광역시");
            provinceMap.put("광주", "광주광역시");
            provinceMap.put("울산", "울산광역시");
            provinceMap.put("전남", "전라남도");
            provinceMap.put("경남", "경상남도");
            provinceMap.put("경북", "경상북도");
            provinceMap.put("충남", "충청남도");
            provinceMap.put("충북", "충청북도");
            provinceMap.put("경기", "경기도");
            provinceMap.put("전북특별자치도", "전북특별자치도");
            provinceMap.put("제주특별자치도", "제주특별자치도");
            provinceMap.put("강원특별자치도", "강원특별자치도");
            provinceMap.put("세종특별자치시", "세종특별자치시");
            return provinceMap.getOrDefault(province, province);
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
}
