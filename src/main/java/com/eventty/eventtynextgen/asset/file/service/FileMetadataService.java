package com.eventty.eventtynextgen.asset.file.service;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import java.util.List;

public interface FileMetadataService {

    FileMetadata save(Long userId, String fileName, String contentType, String fileUrl);

    FileMetadata findById(Long fileMetadataId);

    List<FileMetadata> findAllByUserId(Long userId);

    void deleteById(Long fileMetadataId);
}
