package com.eventty.eventtynextgen.asset.image;


import com.eventty.eventtynextgen.asset.image.annotation.AssetImageApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@AssetImageApiV1
@RequiredArgsConstructor
@Tag(name = "파일 비디오 작업 API", description = "외부 스토리지를 이용하여 파일 관련 작업을 수행하는 API 모음")
public class AssetImageController {

    private final AssetImageService assetImageService;
}
