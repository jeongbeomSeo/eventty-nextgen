package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetDeleteFileResponseView(
    @Schema(description = "파일 메타데이터 ID")
    Long fileMetadataId
) {

}
