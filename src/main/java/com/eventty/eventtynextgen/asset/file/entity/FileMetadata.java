package com.eventty.eventtynextgen.asset.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file-metadata", indexes = {
    @Index(name = "idx_file_metadata_user_id_file_name", columnList = "user_id, file_name", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Builder
    private FileMetadata(Long userId, String fileName, String contentType, Long fileSize, String fileUrl) {
        this.userId = userId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }

    public static FileMetadata of(String fileName, String contentType, Long fileSize, String fileUrl) {
        return FileMetadata.builder()
            .fileName(fileName)
            .contentType(contentType)
            .fileSize(fileSize)
            .fileUrl(fileUrl)
            .build();
    }
}
