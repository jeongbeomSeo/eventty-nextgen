package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.file.annotation.AssetFileApiV1;
import com.eventty.eventtynextgen.asset.file.response.AssetDownloadStreamingFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFilesResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 작업 API", description = "외부 스토리지를 이용하여 파일 관련 작업을 수행하는 API 모음")
@AssetFileApiV1
@RequiredArgsConstructor
public class AssetFileController {

    private final AssetFileService assetFileService;

    @LoginRequired(requireHost = true, requireAdmin = true)
    @GetMapping("/{file-name}")
    public ResponseEntity<AssetGetAssetFileResponseView> getAssetFile(@PathVariable(value = "file-name") String fileName) {
        return ResponseEntity.ok().build();
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @GetMapping("/{file-names}")
    public ResponseEntity<AssetGetAssetFilesResponseView> getAssetFiles(@PathVariable(value = "file-names") List<String> fileNames) {
        return ResponseEntity.ok().build();
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/multipart-file/{context}")
    public ResponseEntity<AssetUploadAssetFile> uploadMultipartFile(@RequestPart("file") MultipartFile file, @PathVariable String context) {

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadMultipartFile(file, context);

        return ResponseEntity.ok(assetUploadAssetFile);
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/multipart-files/{context}")
    public ResponseEntity<List<AssetUploadAssetFile>> uploadMultipartFiles(@RequestPart("files") List<MultipartFile> files, @PathVariable String context) {

        List<AssetUploadAssetFile> assetUploadAssetFiles = this.assetFileService.uploadMultipartFiles(files, context);

        return ResponseEntity.ok(assetUploadAssetFiles);
    }

    @Deprecated
    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/stream-file/{context}")
    public ResponseEntity<AssetUploadAssetFile> uploadStreamFile(HttpServletRequest request, @PathVariable String context) {

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadStreaming(request, context);

        return ResponseEntity.ok(assetUploadAssetFile);
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/large-file")
    public ResponseEntity<Void> uploadLargeFile() {
        return ResponseEntity.ok().build();
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @GetMapping("/exists/{file-name}")
    public ResponseEntity<Void> fileExists(@PathVariable("file-name") String fileName) {
        return ResponseEntity.ok().build();
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @GetMapping("/download/{file-name}")
    public ResponseEntity<AssetDownloadStreamingFileResponseView> downloadStreamingFile(@PathVariable("file-name") String fileName) {
        return ResponseEntity.ok().build();
    }
}
