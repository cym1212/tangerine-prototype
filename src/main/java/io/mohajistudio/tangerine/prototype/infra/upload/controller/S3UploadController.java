package io.mohajistudio.tangerine.prototype.infra.upload.controller;


import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockImageDTO;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class S3UploadController {

    private final S3UploadService s3UploadService;

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "AWS S3에 이미지 업로드", description = "이미지 값을 넘기면 S3에 이미지를 저장하고 URL과 확장자를 리턴합니다.")
    public ResponseEntity<PlaceBlockImageDTO> uploadImage(@NotNull @RequestPart("imageFile") MultipartFile multipartFile) {

        PlaceBlockImageDTO placeBlockImageDTO = s3UploadService.uploadImageToS3(multipartFile);
        return ResponseEntity.ok(placeBlockImageDTO);
    }
}
