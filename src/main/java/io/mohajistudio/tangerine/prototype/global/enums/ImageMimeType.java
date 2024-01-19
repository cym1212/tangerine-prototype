package io.mohajistudio.tangerine.prototype.global.enums;

import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;

public enum ImageMimeType {
    JPEG(".jpeg"),
    PNG(".png"),
    GIF(".gif"),
    BMP(".bmp"),
    WebP(".webp"),
    JPG(".jpg");

    private final String value;

    ImageMimeType(String value) {
        this.value = value;
    }

    public static ImageMimeType fromValue(String value) {
        for (ImageMimeType mimeType : values()) {
            if (mimeType.value.equalsIgnoreCase(value) || mimeType.name().equals(value)) {
                return mimeType;
            }
        }
        throw new BusinessException("지원하지 않는 이미지 확장자입니다: " + value, ErrorCode.INVALID_INPUT_VALUE);
    }
}
