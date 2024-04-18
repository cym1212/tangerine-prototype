package io.mohajistudio.tangerine.prototype.domain.place.service;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceCategoryRepository;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceRepository;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceCategoryRepository placeCategoryRepository;

    public Page<Place> findPlaceListByPage(String query, Pageable pageable) {
        return placeRepository.findByName(query, pageable);
    }

    public List<PlaceCategory> findPlaceCategoryList() {
        return placeCategoryRepository.findAll();
    }

    public Place addPlace(Place place) {
        if(place.getId() != null) {
            Optional<Place> findPlace = placeRepository.findById(place.getId());
            if(findPlace.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            return findPlace.get();
        }
        return placeRepository.save(place);
    }
}
