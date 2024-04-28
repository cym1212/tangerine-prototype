package io.mohajistudio.tangerine.prototype.infra.upload.service;

import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.infra.upload.config.S3Config;
import io.mohajistudio.tangerine.prototype.infra.upload.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploadService {
    private final S3Client s3Client;
    private final S3Config s3Config;

    public String uploadImage(MultipartFile multipartFile, String imagePath, Long memberId) {
        String key = imagePath + UploadUtils.createFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()), memberId);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucket())
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .key(key)
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILURE);
        }

        return key;
    }

    public String copyImage(String storageKey, String orderImagePath, String newImagePath) {
        if(storageKey.contains(newImagePath)) {
            return storageKey;
        }

        String destinationKey = storageKey.replace(orderImagePath, newImagePath);

        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().sourceBucket(s3Config.getBucket()).destinationBucket(s3Config.getBucket()).sourceKey(storageKey).destinationKey(destinationKey).build();
        s3Client.copyObject(copyObjectRequest);

        return storageKey.replace(orderImagePath, newImagePath);
    }

    public void deleteImage(String storageKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(s3Config.getBucket()).key(storageKey).build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
