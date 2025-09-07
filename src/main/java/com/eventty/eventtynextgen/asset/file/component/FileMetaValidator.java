package com.eventty.eventtynextgen.asset.file.component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class FileMetaValidator {

    private static final Set<String> EXCLUDED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/svg", "image/png", "image/gif", "image/bmp", "image/avif", "image/webp",
        "video/mp4", "video/mpeg", "video/quicktime", "video/webm", "video/x-msvideo"
    ));

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyResult {
        private VerifyFileMetaResult verifyFileMetaResult;
        private String details;
    }

    public enum VerifyFileMetaResult {
        VALID,
        INVALID_SIZE,
        INVALID_CONTENT_TYPE,
        INVALID_EXTENSION
    }

    public VerifyResult validateMultipartFile(MultipartFile file) {
        long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB in bytes
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return new VerifyResult(VerifyFileMetaResult.INVALID_SIZE, "File name: " + file.getName() + ", File size exceeds limit. Max size: 25MB and current size: " + file.getSize());
        }

        if (!isValidContentType(file.getContentType())) {
            return new VerifyResult(VerifyFileMetaResult.INVALID_CONTENT_TYPE, "File name: " + file.getName() + ", Invalid file content type: " + file.getContentType());
        }

        if (!isValidExtension(file.getOriginalFilename())) {
            return new VerifyResult(VerifyFileMetaResult.INVALID_EXTENSION, "File name: " + file.getName() + ", Invalid file extension: " + file.getOriginalFilename());
        }

        return new VerifyResult(VerifyFileMetaResult.VALID, "");
    }

    public VerifyResult validateStreamFile(String contentType) {
        if (!isValidContentType(contentType)) {
            return new VerifyResult(VerifyFileMetaResult.INVALID_CONTENT_TYPE, "Invalid file content type: " + contentType);
        }

        return new VerifyResult(VerifyFileMetaResult.VALID, "");
    }

    private boolean isValidContentType(String contentType) {
        return contentType != null && !EXCLUDED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    private boolean isValidExtension(String filename) {
        if (filename == null) {
            return false;
        }
        String extension = filename.toLowerCase();
        return !extension.endsWith(".jpg") && !extension.endsWith(".jpeg")
            && !extension.endsWith(".png") && !extension.endsWith(".gif")
            && !extension.endsWith(".bmp") && !extension.endsWith(".webp")
            && !extension.endsWith(".avif") && !extension.endsWith(".mp4")
            && !extension.endsWith(".mpeg") && !extension.endsWith(".mov")
            && !extension.endsWith(".webm") && !extension.endsWith(".avi");
    }
}
