package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetDownloadFileResponseView (
    @Schema(name = "파일 메타데이터 id")
    Long fileMetadataId,
    @Schema(name = "파일 이름")
    String fileName,
    @Schema(name = "파일 타입")
    String contentType,
    @Schema(name = "파일 다운로드 링크")
    String downloadLink
) {
}
