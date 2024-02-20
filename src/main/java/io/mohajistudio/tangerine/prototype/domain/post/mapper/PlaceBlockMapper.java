package io.mohajistudio.tangerine.prototype.domain.post.mapper;

import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PlaceBlockMapper {
    PlaceBlockDTO.Details toDetailsDTO(PlaceBlock placeBlock);
}
