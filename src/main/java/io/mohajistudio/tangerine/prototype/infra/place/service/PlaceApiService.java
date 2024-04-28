package io.mohajistudio.tangerine.prototype.infra.place.service;

import io.mohajistudio.tangerine.prototype.infra.place.dto.KakaoPlaceApiDTO;

public interface PlaceApiService {
    KakaoPlaceApiDTO searchPlace(String query, int page, int size);
}
