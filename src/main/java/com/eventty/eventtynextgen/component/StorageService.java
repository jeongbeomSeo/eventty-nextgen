package com.eventty.eventtynextgen.component;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, Purpose purpose);

    void deleteFile(String fileName);

    byte[] downloadFile(String fileName);

    String findFileUrl(String fileName, Purpose purpose);

    FindFileUrlResult findFileUrls(List<String> fileNames, Purpose purpose);

    boolean existsFile(String fileName, Purpose purpose);

    enum Purpose {
        EVENT_IMAGE
    }

    record FindFileUrlResult(List<String> fileUrls, List<String> failedFileNames) {
    }
}
