package com.eventty.eventtynextgen.asset.core;

import jakarta.annotation.Nullable;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageClient {

    UploadFileResult uploadMultipartFile(MultipartFile file, StorageContext context);

    UploadFileResult uploadStreaming(InputStream inputStream, StorageContext context, @Nullable String contentType);

    boolean deleteFile(String fileName, StorageContext context);

    String findFileDownloadLink(String fileName, StorageContext context);

    String findFileUrl(String fileName, StorageContext context);

    FindFileUrlResult findFileUrls(List<String> fileNames, StorageContext context);

    boolean existsFile(String fileName, StorageContext context);

    enum StorageContext {
        FILE,
        LARGE_FILE,
        EVENT_IMAGE,
        EVENT_VIDEO;
    }

    record FindFileUrlResult(List<String> fileUrls, List<String> failedFileNames) {}

    record UploadFileResult(String fileName, String contentType, boolean isSuccess, Exception exception) {

        public static UploadFileResult success(String fileName, String contentType) {
            return new UploadFileResult(fileName, contentType, true, null);
        }

        public static UploadFileResult fail(Exception e) {
            return new UploadFileResult("", "", false, e);
        }
    }
}
