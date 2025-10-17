package com.eventty.eventtynextgen.asset.file.repository;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    FileMetadata findByUserIdAndFileName(Long userId, String fileName);
}
