package io.mohajistudio.tangerine.prototype.domain.placeblockimage.service;

import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.global.enums.ImageMimeType;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
import io.mohajistudio.tangerine.prototype.infra.upload.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaceBlockImageService {
    private final S3UploadService s3UploadService;
    private static final String TEMPORARY_PATH = "temp/";
    private static final String PERMANENT_PATH = "images/";

    public List<PlaceBlockImage> uploadImagesToTemporary(List<MultipartFile> imageFiles, Long memberId) {
        List<PlaceBlockImage> placeBlockImages = new ArrayList<>();

        for (int i = 1; i <= imageFiles.size(); i++) {
            String imageUrl = s3UploadService.uploadImage(imageFiles.get(i - 1), TEMPORARY_PATH, memberId);
            ImageMimeType mimeType = ImageMimeType.fromValue(UploadUtils.getFileExtension(imageUrl));
            PlaceBlockImage placeBlockImage = PlaceBlockImage.builder().imageUrl(imageUrl).imageMimeType(mimeType).build();
            placeBlockImages.add(placeBlockImage);
        }

        return placeBlockImages;
    }

    public void copyImagesToPermanent(Set<PlaceBlockImage> placeBlockImages) {
        placeBlockImages.forEach(placeBlockImage -> {
            if (placeBlockImage.getImageUrl().contains(TEMPORARY_PATH)) {
                String newFileName = copyImageToPermanent(placeBlockImage.getImageUrl());
                placeBlockImage.setImageUrl(newFileName);
            }
        });
    }

    public String copyImageToPermanent(String imageUrl) {
        s3UploadService.copyImage(imageUrl, TEMPORARY_PATH, PERMANENT_PATH);
        return imageUrl.replace(TEMPORARY_PATH, PERMANENT_PATH);
    }
}
