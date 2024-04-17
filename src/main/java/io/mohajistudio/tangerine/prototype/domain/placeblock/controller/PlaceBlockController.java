package io.mohajistudio.tangerine.prototype.domain.placeblock.controller;

import io.mohajistudio.tangerine.prototype.domain.placeblock.service.PlaceBlockService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockDTO;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PlaceBlockMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place-blocks")
@RequiredArgsConstructor
@Tag(name = "PlaceBlock", description = "PlaceBlock API")
public class PlaceBlockController {
    private final PlaceBlockService placeBlockService;
    private final PlaceBlockMapper placeBlockMapper;

    @GetMapping("/bounds")
    @Operation(summary = "장소 카테고리 목록 조회", description = "장소 카테고리 목록을 조회합니다.")
    public List<PlaceBlockDTO.Details> placeListInBounds(@RequestParam("minLng") double minLng, @RequestParam("minLat") double minLat, @RequestParam("maxLng") double maxLng, @RequestParam("maxLat") double maxLat) {
        List<PlaceBlock> placeListBlockInBounds = placeBlockService.findPlaceBlockListInBounds(minLng, minLat, maxLng, maxLat);
        return placeListBlockInBounds.stream().map(placeBlockMapper::toDetailsDTO).toList();
    }
}
