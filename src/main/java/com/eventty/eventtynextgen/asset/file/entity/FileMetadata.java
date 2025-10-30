package com.eventty.eventtynextgen.asset.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "file_metadata", indexes = {
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

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private FileMetadata(Long userId, String fileName, String contentType, String fileUrl) {
        this.userId = userId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileUrl = fileUrl;
        this.isDeleted = false;
    }

    public static FileMetadata of(Long userId, String fileName, String contentType, String fileUrl) {
        return FileMetadata.builder()
            .userId(userId)
            .fileName(fileName)
            .contentType(contentType)
            .fileUrl(fileUrl)
            .build();
    }

    public String getFileName() {
        if (fileName == null) {
            return null;
        }

        int idx = fileName.indexOf("/");

        if (idx == -1) {
            return fileName;
        } else {
            return fileName.substring(idx + 1);
        }
    }

    public void updateDeleteStatus(FileMetadataStatus status) {
        if (status == FileMetadataStatus.ACTIVE) {
            this.isDeleted = false;
            this.deletedAt = null;
        } else if (status == FileMetadataStatus.DELETED) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    public enum FileMetadataStatus {
        ACTIVE,
        DELETED
    }
}
