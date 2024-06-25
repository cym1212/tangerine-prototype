package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.mapper.PlaceMapper;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
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


@Mapper(uses = {PlaceMapper.class})
public interface PostMapper {
    String regex = "^(?<province>\\S+)\\s+(?<city>\\S+)\\s+(?<district>\\S+)\\s*(?<detail>.+)?$";

    Pattern pattern = Pattern.compile(regex);

    PostDTO.Compact toCompactDTO(Post post);

    PostDTO.Details toDetailsDTO(Post post);

    Post toEntity(PostDTO.Details postDetailsDTO);

    Post toEntity(PostDTO.Add postAddDTO);

    @Mapping(target = "representativePlaceBlockImageOrderNumber", ignore = true)
    @Mapping(target = "postId", source = "post.id")
    PlaceBlockDTO.Details toDTO(PlaceBlock placeBlock);

}
