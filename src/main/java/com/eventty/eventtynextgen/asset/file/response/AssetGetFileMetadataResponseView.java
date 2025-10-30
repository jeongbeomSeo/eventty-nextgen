package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetGetFileMetadataResponseView(
    @Schema(description = "파일 메타데이터 ID")
    Long fileMetadataId,
    @Schema(description = "파일 이름")
    String fileName,
    @Schema(description = "파일 콘텐츠 타입")
    String contentType,
    @Schema(description = "파일 URL")
    String fileUrl
) {
}
