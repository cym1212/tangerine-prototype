package io.mohajistudio.tangerine.prototype.infra.upload.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.ImageMimeType;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.upload.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final S3Config s3Config;
    private final AmazonS3Client amazonS3Client;


    public List<PlaceBlockImage> uploadImagesToS3(List<MultipartFile> multipartFiles) throws IOException {
        List<PlaceBlockImage> placeBlockImages = new ArrayList<>();
        short orderNumberCounter = 1;

        for (MultipartFile multipartFile : multipartFiles) {
            PlaceBlockImage placeBlockImage = uploadImageToS3(multipartFile, orderNumberCounter++);
            placeBlockImages.add(placeBlockImage);
        }

        return placeBlockImages;
    }

    public PlaceBlockImage uploadImageToS3(MultipartFile multipartFile, short orderNumber) throws IOException {

        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        String changedName = changedImageName(ext);
        ObjectMetadata metadata = getObjectMetadata(multipartFile);

        amazonS3Client.putObject(s3Config.getBucket(), changedName, multipartFile.getInputStream(), metadata);
        String uploadFileUrl = amazonS3Client.getUrl(s3Config.getBucket(), changedName).toString();

        return PlaceBlockImage.builder()
                .imageUrl(uploadFileUrl)
                .imageMimeType(ImageMimeType.PNG)
                .orderNumber(orderNumber)
                .build();
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    private String changedImageName(String ext) {
        String random = UUID.randomUUID().toString();
        return random + ext;
    }
}
