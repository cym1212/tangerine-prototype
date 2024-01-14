package io.mohajistudio.tangerine.prototype.global.enums;

public enum ImageMimeType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),

    JPG("image/jpg");

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
        throw new IllegalArgumentException("지원하지 않는 이미지 확장자입니다: " + value);
    }
}
