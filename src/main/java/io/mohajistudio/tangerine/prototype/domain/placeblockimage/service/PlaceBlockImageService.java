package io.mohajistudio.tangerine.prototype.domain.placeblockimage.service;

import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.repository.PlaceBlockImageRepository;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
import io.mohajistudio.tangerine.prototype.infra.upload.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaceBlockImageService {
    private final S3UploadService s3UploadService;
    private final PlaceBlockImageRepository placeBlockImageRepository;

    public List<PlaceBlockImage> uploadImagesToTemporary(List<MultipartFile> imageFiles, Long memberId) {
        List<PlaceBlockImage> placeBlockImages = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            String storageKey = s3UploadService.uploadImage(imageFile, UploadUtils.TEMPORARY_PATH, memberId);
            PlaceBlockImage placeBlockImage = PlaceBlockImage.builder().storageKey(storageKey).build();
            placeBlockImages.add(placeBlockImage);
        }

        return placeBlockImages;
    }

    public void copyImagesToPermanent(Set<PlaceBlockImage> placeBlockImages) {
        placeBlockImages.forEach(placeBlockImage -> {
            if (placeBlockImage.getStorageKey().contains(UploadUtils.TEMPORARY_PATH)) {
                String newFileName = copyImageToPermanent(placeBlockImage.getStorageKey());
                placeBlockImage.setStorageKey(newFileName);
            }
        });
    }

    public String copyImageToPermanent(String storageKey) {
        return s3UploadService.copyImage(storageKey, UploadUtils.TEMPORARY_PATH, UploadUtils.IMAGES_PATH);
    }

    public void modifyPlaceBlockImage(PlaceBlock placeBlock, PlaceBlockImage placeBlockImage) {
        if (placeBlockImage.getId() == null) {
            placeBlockImage.setPlaceBlock(placeBlock);
            placeBlockImageRepository.save(placeBlockImage);
        } else {
            Optional<PlaceBlockImage> findPlaceBlockImage = placeBlockImageRepository.findById(placeBlockImage.getId());
            if (findPlaceBlockImage.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            placeBlockImageRepository.update(findPlaceBlockImage.get().getId(), placeBlockImage.getStorageKey(), placeBlockImage.getOrderNumber());
        }
    }

}
