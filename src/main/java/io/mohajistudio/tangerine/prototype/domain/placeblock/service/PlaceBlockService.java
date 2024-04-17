package io.mohajistudio.tangerine.prototype.domain.placeblock.service;

import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.placeblock.repository.PlaceBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceBlockService {
    private final PlaceBlockRepository placeBlockRepository;

    public List<PlaceBlock> findPlaceBlockListInBounds(double minLng, double minLat, double maxLng, double maxLat) {
        return placeBlockRepository.findAllInBounds(minLng, minLat, maxLng, maxLat);
    }
}
