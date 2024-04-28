package io.mohajistudio.tangerine.prototype.domain.place.controller;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.dto.PlaceCategoryDTO;
import io.mohajistudio.tangerine.prototype.domain.place.mapper.PlaceCategoryMapper;
import io.mohajistudio.tangerine.prototype.domain.place.mapper.PlaceMapper;
import io.mohajistudio.tangerine.prototype.domain.place.service.PlaceService;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceDTO;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PostMapper;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.mohajistudio.tangerine.prototype.global.enums.PlaceProvider;
import io.mohajistudio.tangerine.prototype.infra.place.dto.KakaoPlaceApiDTO;
import io.mohajistudio.tangerine.prototype.infra.place.service.PlaceApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "Place API")
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceMapper placeMapper;
    private final PostMapper postMapper;
    private final PlaceCategoryMapper placeCategoryMapper;
    private final PlaceApiService placeApiService;

    @GetMapping
    @Operation(summary = "장소 목록 조회", description = "검색어를 query에 담아 page와 size 값을 넘기면 페이징 된 장소 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<PlaceDTO.Details> placeListByPage(@RequestParam("query") String query, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        return placeService.findPlaceListByPage(query, pageable).map(postMapper::toPlaceDetailsDTO);
    }

    @GetMapping("/bounds")
    @Operation(summary = "장소 카테고리 목록 조회", description = "장소 카테고리 목록을 조회합니다.")
    public List<PlaceDTO.Details> placeListInBounds(@RequestParam("minLng") double minLng, @RequestParam("minLat") double minLat, @RequestParam("maxLng") double maxLng, @RequestParam("maxLat") double maxLat) {
        List<Place> placeListBlockInBounds = placeService.findPlaceListInBounds(minLng, minLat, maxLng, maxLat);
        return placeListBlockInBounds.stream().map(postMapper::toPlaceDetailsDTO).toList();
    }

    @GetMapping("/kakao")
    @Operation(summary = "카카오 장소 목록 조회", description = "검색어를 query에 담아 page와 size 값을 넘기면 페이징 된 장소 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<PlaceDTO.Details> kakaoPlaceListByPage(@RequestParam("query") String query, @ModelAttribute PageableParam pageableParam) {
        KakaoPlaceApiDTO kakaoPlaceApiDTO = placeApiService.searchPlace(query, pageableParam.getPage() + 1, pageableParam.getSize());

        List<Place> placeList = kakaoPlaceApiDTO.getDocuments().stream().map(document -> {
            Place place = placeMapper.toEntity(document);
            place.setPlaceSearchProvider(PlaceProvider.KAKAO);
            return place;
        }).toList();

        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        List<PlaceDTO.Details> placeDetailsDTOList = placeList.stream().map(postMapper::toPlaceDetailsDTO).toList();

        return new PageImpl<>(placeDetailsDTOList, pageable, kakaoPlaceApiDTO.getMeta().getTotalCount());
    }

    @GetMapping("/categories")
    @Operation(summary = "장소 카테고리 목록 조회", description = "장소 카테고리 목록을 조회합니다.")
    public List<PlaceCategoryDTO> placeCategoryList() {
        return placeService.findPlaceCategoryList().stream().map(placeCategoryMapper::toDTO).toList();
    }
}