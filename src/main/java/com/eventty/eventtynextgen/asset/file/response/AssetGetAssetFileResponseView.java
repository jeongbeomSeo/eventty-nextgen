package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetGetAssetFileResponseView(
    @Schema(description = "파일 공개 URL 주소")
    String fileUrl
){
}
