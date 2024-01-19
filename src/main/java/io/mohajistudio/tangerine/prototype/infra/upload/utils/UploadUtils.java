package io.mohajistudio.tangerine.prototype.infra.upload.utils;

import java.util.UUID;

public class UploadUtils {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String FILE_PATH_SEPARATOR = "/";
    private static final String MEMBER_UUID_SEPARATOR = "-";

    public static String createFileName(String originalFileName, Long memberId) {
        String ext = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return memberId + MEMBER_UUID_SEPARATOR + uuid + ext;
    }

    public static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
    }

    public static String extractImagePath(String originalFileName) {
        String[] parts = originalFileName.split(FILE_PATH_SEPARATOR);

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = parts.length - 2; i < parts.length; i++) {
            pathBuilder.append(parts[i]);
            if (i < parts.length - 1) {
                pathBuilder.append(FILE_PATH_SEPARATOR);
            }
        }

        return pathBuilder.toString();
    }
}