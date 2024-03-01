package io.mohajistudio.tangerine.prototype.infra.upload.utils;

import java.util.UUID;

public class UploadUtils {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String MEMBER_UUID_SEPARATOR = "-";
    public static final String TEMPORARY_PATH = "temp/";
    public static final String IMAGES_PATH = "images/";
    public static final String PROFILE_IMAGES_PATH = "profile-images/";

    public static String createFileName(String originalFileName, Long memberId) {
        String ext = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return memberId + MEMBER_UUID_SEPARATOR + uuid + ext;
    }

    public static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
    }
}