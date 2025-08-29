package com.eventty.eventtynextgen.asset.core;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageClient {

    String uploadFile(MultipartFile file, Context context);

    boolean deleteFile(String fileName, Context context);

    String findFileDownloadLink(String fileName, Context context);

    String findFileUrl(String fileName, Context context);

    FindFileUrlResult findFileUrls(List<String> fileNames, Context context);

    boolean existsFile(String fileName, Context context);

    enum Context {
        FILE,
        LARGE_FILE,
        EVENT_IMAGE,
        EVENT_VIDEO
    }

    record FindFileUrlResult(List<String> fileUrls, List<String> failedFileNames) {
    }
}
