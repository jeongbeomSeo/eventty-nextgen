package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AssetGetAssetFilesResponseView(
    @Schema(description = "파일 공개 URL 주소 리스트")
    List<String> fileUrls,
    @Schema(description = "접근할 수 없는 파일 이름 리스트")
    List<String> failedFileNames
) {

}
