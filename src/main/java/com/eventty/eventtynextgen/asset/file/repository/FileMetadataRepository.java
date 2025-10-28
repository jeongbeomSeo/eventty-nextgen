package com.eventty.eventtynextgen.asset.file.repository;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUserId(Long userId);
}
