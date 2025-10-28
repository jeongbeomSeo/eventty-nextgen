package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.file.annotation.AssetFileApiV1;
import com.eventty.eventtynextgen.asset.file.request.AssetFileUploadMultipartFileRequestCommand;
import com.eventty.eventtynextgen.asset.file.response.AssetDownloadStreamingFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetFindFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFilesResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 작업 API", description = "외부 스토리지를 이용하여 파일 관련 작업을 수행하는 API 모음")
@AssetFileApiV1
@RequiredArgsConstructor
public class AssetFileController {

    private final AssetFileService assetFileService;

    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping(value = "/multipart-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssetUploadAssetFile> uploadMultipartFile(
        @RequestPart(value = "file") MultipartFile file,
        @RequestPart(value = "requestCommand") @Valid AssetFileUploadMultipartFileRequestCommand requestCommand) {

        Long userId = SessionContextHolder.getContext().getUserId();

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadMultipartFile(file, userId, requestCommand.fileName());

        return ResponseEntity.ok(assetUploadAssetFile);
    }

    /**
     * 복합 파일 동시 업로드 API입니다.
     *
     * @deprecated {@link #uploadMultipartFile(MultipartFile, AssetFileUploadMultipartFileRequestCommand)}를 사용하세요.
     *
     * <p>
     * 복합 업로드는 부분 실패 처리, 재시도, 자원 관리가 복잡해 운영 리스크가 큽니다.<br> 단일 파일 업로드를 파일 수만큼 병렬 호출하는 방식을 권장합니다.
     * </p>
     *
     * <ul>
     *   <li>파일별 실패, 재시도, 진행률 관리가 단순합니다.</li>
     *   <li>서버 자원(메모리/스레드) 고갈 위험이 낮고, 레이트 리밋·타임아웃·모니터링을 요청 단위로 적용하기 쉽습니다.</li>
     *   <li>대용량 전송은 Pre-signed URL과 Resumable upload 조합을 추천합니다.</li>
     *   <li>기존 엔드포인트는 하위 호환을 위해 일정 기간 유지하되, 신규 연동은 단일 업로드 API를 사용해주세요.</li>
     * </ul>
     */
    @Deprecated
    @LoginRequired(requireHost = true, requireAdmin = true)
        @PostMapping(value = "/multipart-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssetUploadAssetFile>> uploadMultipartFiles(@RequestPart("files") List<MultipartFile> files) {

        List<AssetUploadAssetFile> assetUploadAssetFiles = this.assetFileService.uploadMultipartFiles(files);

        return ResponseEntity.ok(assetUploadAssetFiles);
    }

    /**
     * ServletInputStream을 통해 원시 바이트 스트림을 업로드합니다.
     *
     * <p>제약 사항:</p>
     * <ul>
     *   <li>파일 크기: 스트림을 모두 읽기 전에는 총 크기를 알 수 없음</li>
     *   <li>Content-Type: HTTP 헤더 또는 인자로 신뢰해야 하며 검증이 어려움</li>
     *   <li>파일 확장자: 파일명이 없어 확장자 추출 불가</li>
     * </ul>
     *
     * @deprecated 제약 사항으로 인해 사용이 권장되지 않습니다. 대신 {@link #uploadMultipartFile(MultipartFile, AssetFileUploadMultipartFileRequestCommand)}를 사용하세요.
     */
    @Deprecated
    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/stream-file")
    public ResponseEntity<AssetUploadAssetFile> uploadStreamFile(HttpServletRequest request) {

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadStreaming(request);

        return ResponseEntity.ok(assetUploadAssetFile);
    }

    @LoginRequired(requireAdmin = true, requireHost = true)
    @GetMapping("/metadata/{userId}")
    public ResponseEntity<AssetFindFileMetadataResponseView> findFileMetadata(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(this.assetFileService.findFileMetadata(userId));
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @GetMapping("/metadata/{userId}/{fileMetadataId}")
    public ResponseEntity<AssetGetFileMetadataResponseView> getFileMetadata(@PathVariable("userId") Long userId, @PathVariable("fileMetadataId") Long fileMetadataId) {
        return ResponseEntity.ok(this.assetFileService.getFileMetadata(userId, fileMetadataId));
    }
}
