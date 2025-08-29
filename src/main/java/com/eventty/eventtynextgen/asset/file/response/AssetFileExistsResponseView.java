package com.eventty.eventtynextgen.asset.file.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssetFileExistsResponseView (
    @Schema(name = "파일 존재 여부")
    boolean exists
){

}
