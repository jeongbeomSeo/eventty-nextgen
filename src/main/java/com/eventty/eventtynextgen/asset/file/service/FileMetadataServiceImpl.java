package com.eventty.eventtynextgen.asset.file.service;

import static com.eventty.eventtynextgen.base.exception.enums.AssetErrorType.*;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata.FileMetadataStatus;
import com.eventty.eventtynextgen.asset.file.repository.FileMetadataRepository;
import com.eventty.eventtynextgen.base.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    @Override
    @Transactional
    public FileMetadata save(Long userId, String fileName, String contentType, String fileUrl) {

        FileMetadata fileMetadata = FileMetadata.of(userId, fileName, contentType, fileUrl);

        return this.fileMetadataRepository.save(fileMetadata);
    }

    @Override
    public FileMetadata findById(Long fileMetadataId) {
        return this.fileMetadataRepository.findById(fileMetadataId)
            .orElseThrow(() -> CustomException.of(HttpStatus.NOT_FOUND, NOT_FOUND_FILE_METADATA));
    }

    @Override
    public List<FileMetadata> findAllByUserId(Long userId) {
        return this.fileMetadataRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteById(Long fileMetadataId) {
        FileMetadata fileMetadata = this.fileMetadataRepository.findById(fileMetadataId)
            .orElseThrow(() -> CustomException.badRequest(NOT_FOUND_FILE_METADATA, "fileMetadataId: " + fileMetadataId));

        if (fileMetadata.isDeleted()) {
            throw CustomException.badRequest(ALREADY_FILE_DELETED, "fileMetadataId: " + fileMetadataId);
        }

        fileMetadata.updateDeleteStatus(FileMetadataStatus.DELETED);
    }
}
