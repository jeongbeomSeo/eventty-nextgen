package com.eventty.eventtynextgen.asset.file.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AssetFileUploadMultipartFileRequestCommand (
    @Schema(description = "파일 이름")
    @NotBlank(message = "파일 이름은 비어 있을 수 없습니다.")
    String fileName
){

}
