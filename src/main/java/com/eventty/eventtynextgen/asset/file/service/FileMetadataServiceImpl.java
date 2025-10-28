package com.eventty.eventtynextgen.asset.file.service;

import static com.eventty.eventtynextgen.base.exception.enums.AssetErrorType.*;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.repository.FileMetadataRepository;
import com.eventty.eventtynextgen.base.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    @Override
    @Transactional
    public FileMetadata save(Long userId, String fileName, String contentType, Long fileSize, String fileUrl) {

        FileMetadata fileMetadata = FileMetadata.of(userId, fileName, contentType, fileSize, fileUrl);

        return fileMetadataRepository.save(fileMetadata);
    }

    @Override
    public FileMetadata findById(Long fileMetadataId) {
        return fileMetadataRepository.findById(fileMetadataId)
            .orElseThrow(() -> CustomException.badRequest(NOT_FOUND_FILE_METADATA));
    }

    @Override
    public List<FileMetadata> findAllByUserId(Long userId) {
        return fileMetadataRepository.findByUserId(userId);
    }
}
