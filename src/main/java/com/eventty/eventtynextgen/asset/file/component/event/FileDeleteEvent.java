package com.eventty.eventtynextgen.asset.file.component.event;

public record FileDeleteEvent (
    Long fileMetadataId,
    Long userId,
    String fileName
){
}
