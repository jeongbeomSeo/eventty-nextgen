package com.eventty.eventtynextgen.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsStorageService implements StorageService {

    @Override
    public String uploadFile(MultipartFile file, Purpose purpose) {
        return "";
    }

    @Override
    public void deleteFile(String fileUrl) {

    }

    @Override
    public byte[] downloadFile(String fileUrl) {
        return new byte[0];
    }

    @Override
    public boolean fileExists(String fileUrl) {
        return true;
    }
}
