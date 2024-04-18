package io.mohajistudio.tangerine.prototype.domain.placeblock.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/place-blocks")
@RequiredArgsConstructor
@Tag(name = "PlaceBlock", description = "PlaceBlock API")
public class PlaceBlockController {
}
