package com.eventty.eventtynextgen.asset.file.service;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;

public interface FileMetadataService {

    FileMetadata save(Long userId, String fileName, String contentType, Long fileSize, String fileUrl);
}
