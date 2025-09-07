package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetUploadAssetFile(
    @Schema(name = "파일 이름")
    String fileName,
    @Schema(name = "파일 타입")
    String contentType
) {
}
