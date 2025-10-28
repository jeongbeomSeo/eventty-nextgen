package com.eventty.eventtynextgen.asset.file.fixture;

import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata.FileMetadataStatus;

public class FileMetadataFixture {

    public static FileMetadata createFileMetadata(Long userId, String fileName) {
        return FileMetadata.of(userId, fileName, "application/json", null, "http://example.com/file");
    }

    public static FileMetadata createDeletedFileMetadata(Long userId, String fileName) {
        FileMetadata fileMetadata = createFileMetadata(userId, fileName);
        fileMetadata.updateDeleteStatus(FileMetadataStatus.DELETED);
        return fileMetadata;
    }

}
