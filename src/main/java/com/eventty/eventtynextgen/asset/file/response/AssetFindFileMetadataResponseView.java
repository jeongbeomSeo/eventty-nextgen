package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AssetFindFileMetadataResponseView(
    @Schema(description = "사용자 ID")
    Long userId,
    @Schema(description = "파일 메타데이터 리스트")
    List<FileMetadata> fileMetadataList
) {
    public record FileMetadata(
        @Schema(description = "파일 메타데이터 ID")
        Long fileMetadataId,
        @Schema(description = "파일 이름")
        String fileName,
        @Schema(description = "파일 콘텐츠 타입")
        String contentType,
        @Schema(description = "파일 크기")
        Long fileSize,
        @Schema(description = "파일 URL")
        String fileUrl
    ) {}
}
