package io.mohajistudio.tangerine.prototype.domain.placeblockimage.controller;

import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.service.PlaceBlockImageService;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockImageDTO;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PlaceBlockImageMapper;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts/place-blocks/place-block-images")
@RequiredArgsConstructor
@Tag(name = "PlaceBlockImage", description = "PlaceBlockImage API")
public class PlaceBlockImageController {
    private final PlaceBlockImageService placeBlockImageService;
    private final PlaceBlockImageMapper placeBlockImageMapper;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "AWS S3에 이미지 업로드", description = "이미지 값을 넘기면 S3에 이미지를 저장하고 PlaceBlockImage를 리턴합니다.")
    public List<PlaceBlockImageDTO.Upload> uploadImages(@RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new BusinessException(ErrorCode.MULTIPART_FILE_EXCEPTION);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        List<PlaceBlockImage> placeBlockImages = placeBlockImageService.uploadImagesToTemporary(imageFiles, securityMember.getId());
        return placeBlockImages.stream().map(placeBlockImageMapper::toDTO).toList();
    }
}
