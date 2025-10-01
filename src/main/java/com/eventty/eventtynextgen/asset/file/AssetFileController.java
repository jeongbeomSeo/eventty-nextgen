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
    @PostMapping(value = "/multipart-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssetUploadAssetFile> uploadMultipartFile(@RequestPart("file") MultipartFile file) {

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadMultipartFile(file);

        return ResponseEntity.ok(assetUploadAssetFile);
    }

    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping(value ="/multipart-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssetUploadAssetFile>> uploadMultipartFiles(@RequestPart("files") List<MultipartFile> files) {

        List<AssetUploadAssetFile> assetUploadAssetFiles = this.assetFileService.uploadMultipartFiles(files);

        return ResponseEntity.ok(assetUploadAssetFiles);
    }

    @Deprecated
    @LoginRequired(requireHost = true, requireAdmin = true)
    @PostMapping("/stream-file")
    public ResponseEntity<AssetUploadAssetFile> uploadStreamFile(HttpServletRequest request) {

        AssetUploadAssetFile assetUploadAssetFile = this.assetFileService.uploadStreaming(request);

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
