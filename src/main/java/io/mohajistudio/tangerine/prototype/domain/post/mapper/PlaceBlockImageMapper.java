package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockImageDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PlaceBlockImageMapper {
    PlaceBlockImageDTO.Upload toDTO(PlaceBlockImage placeBlockImage);
}
