package com.eventty.eventtynextgen.component;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, Context context);

    void deleteFile(String fileName);

    String findFileDownloadlink(String fileName, Context context);

    String findFileUrl(String fileName, Context context);

    FindFileUrlResult findFileUrls(List<String> fileNames, Context context);

    boolean existsFile(String fileName, Context context);

    enum Context {
        EVENT_IMAGE
    }

    record FindFileUrlResult(List<String> fileUrls, List<String> failedFileNames) {
    }
}
