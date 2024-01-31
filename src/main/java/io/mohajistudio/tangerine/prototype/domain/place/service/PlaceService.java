package io.mohajistudio.tangerine.prototype.domain.place.service;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceCategoryRepository;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceRepository;
import io.mohajistudio.tangerine.prototype.global.enums.PlaceSearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    public final PlaceCategoryRepository placeCategoryRepository;

    public Page<Place> findPlaceListByPage(String query, Pageable pageable) {
        return placeRepository.findByName(query, pageable);
    }

    public List<PlaceCategory> findPlaceCategoryList() {
        return placeCategoryRepository.findAll();
    }
}
