package io.mohajistudio.tangerine.prototype.global.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import io.mohajistudio.tangerine.prototype.global.common.CustomMultipartFile;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.imgscalr.Scalr;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUtils {

    @Transactional
    public MultipartFile resizeImage(MultipartFile multipartFile, int targetWidth, int targetHeight) {
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String ext = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf(".") + 1);
            BufferedImage originalImage;

            originalImage = ImageIO.read(multipartFile.getInputStream());

            Metadata metadata = ImageMetadataReader.readMetadata(multipartFile.getInputStream());
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0Directory != null) {
                int orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                originalImage = rotateImage(originalImage, orientation);
            }

            int originalImageWidth = originalImage.getWidth();
            int originalImageHeight = originalImage.getHeight();

            if (originalImageWidth < targetWidth && originalImageHeight < targetHeight) {
                return multipartFile;
            }

            MarvinImage marvinImage = new MarvinImage(originalImage);

            int newTargetWidth, newTargetHeight;
            if (originalImageWidth > originalImageHeight) {
                newTargetWidth = targetWidth;
                newTargetHeight = Math.round((float) originalImageHeight * targetWidth / originalImageWidth);
            } else {
                newTargetWidth = Math.round((float) originalImageWidth * targetHeight / originalImageHeight);
                newTargetHeight = targetHeight;
            }

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", newTargetWidth);
            scale.setAttribute("newHeight", newTargetHeight);
            scale.process(marvinImage.clone(), marvinImage, null, null, false);
            BufferedImage bufferedImageNoAlpha = marvinImage.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImageNoAlpha, ext, baos);
            baos.flush();

            return new CustomMultipartFile(multipartFile.getName(), originalFileName, multipartFile.getContentType(), baos.toByteArray());
        } catch (IOException | ImageProcessingException | MetadataException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_RESIZE_FAIL);
        }
    }

    private BufferedImage rotateImage(BufferedImage originalImage, int orientation) {
        switch (orientation) {
            case 1:
                break;
            case 3:
                originalImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_180, (BufferedImageOp) null);
                break;
            case 6:
                originalImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_90, (BufferedImageOp) null);
                break;
            case 8:
                originalImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_270, (BufferedImageOp) null);
                break;
            default:
                break;
        }
        return originalImage;
    }
}
