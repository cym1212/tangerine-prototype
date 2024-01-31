package io.mohajistudio.tangerine.prototype.domain.place.service;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceCategoryRepository;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Transactional
    public void savePlaceListFromProvider(List<Place> placeList) {
        placeList.forEach(place -> {
            Optional<Place> findPlace = placeRepository.findByProviderId(place.getProviderId());
            if (findPlace.isPresent()) {
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                Place oldPlace = findPlace.get();
                if (oldPlace.getModifiedAt().isBefore(thirtyDaysAgo)) {
                placeRepository.update(oldPlace.getId(), place.getName(), place.getCoordinate().getX(), place.getCoordinate().getY(), place.getAddressProvince(), place.getAddressCity(), place.getAddressDistrict(), place.getAddressDetail(), place.getRoadAddress(), place.getDescription(), place.getLink());
                }
            } else {
                placeRepository.save(place);
            }
        });
    }
}
