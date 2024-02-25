package io.mohajistudio.tangerine.prototype.domain.placeblockimage.service;

import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
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

        for (MultipartFile imageFile : imageFiles) {
            String storageKey = s3UploadService.uploadImage(imageFile, TEMPORARY_PATH, memberId);
            PlaceBlockImage placeBlockImage = PlaceBlockImage.builder().storageKey(storageKey).build();
            placeBlockImages.add(placeBlockImage);
        }

        return placeBlockImages;
    }

    public void copyImagesToPermanent(Set<PlaceBlockImage> placeBlockImages) {
        placeBlockImages.forEach(placeBlockImage -> {
            if (placeBlockImage.getStorageKey().contains(TEMPORARY_PATH)) {
                String newFileName = copyImageToPermanent(placeBlockImage.getStorageKey());
                placeBlockImage.setStorageKey(newFileName);
            }
        });
    }

    public String copyImageToPermanent(String storageKey) {
        return s3UploadService.copyImage(storageKey, TEMPORARY_PATH, PERMANENT_PATH);
    }
}
