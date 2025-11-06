package com.eventty.eventtynextgen.shared.outbox.events;

import lombok.Getter;

@Getter
public class FileDeletedEvent {

    private final Long fileMetadataId;
    private final String fileName;

    private FileDeletedEvent(Long fileMetadataId, String fileName) {
        this.fileMetadataId = fileMetadataId;
        this.fileName = fileName;
    }

    public static FileDeletedEvent of(Long fileMetadataId, String fileName) {
        return new FileDeletedEvent(fileMetadataId, fileName);
    }
}
