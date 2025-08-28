package com.eventty.eventtynextgen.component;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, Purpose purpose);

    void deleteFile(String fileUrl);

    byte[] downloadFile(String fileUrl);

    boolean fileExists(String fileUrl, Purpose purpose);

    enum Purpose {
        EVENT_IMAGE
    }
}
