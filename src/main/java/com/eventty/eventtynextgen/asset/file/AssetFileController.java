package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.file.annotation.AssetFileApiV1;
import com.eventty.eventtynextgen.asset.file.response.AssetDownloadStreamingFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetAssetFilesResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
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

@Tag(name = "파일 이미지 작업 API", description = "외부 스토리지를 이용하여 파일 관련 작업을 수행하는 API 모음")
@AssetFileApiV1
@RequiredArgsConstructor
public class AssetFileController {

    @GetMapping("/{file-name}")
    public ResponseEntity<AssetGetAssetFileResponseView> getAssetFile(@PathVariable(value = "file-name") String fileName) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{file-names}")
    public ResponseEntity<AssetGetAssetFilesResponseView> getAssetFiles(@PathVariable(value = "file-names") List<String> fileNames) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/multipart-file/{context}")
    public ResponseEntity<AssetUploadAssetFile> uploadMultipartFile(@RequestPart("file") MultipartFile file, @PathVariable String context) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/multipart-files/{context}")
    public ResponseEntity<AssetUploadAssetFile> uploadMultipartFile(@RequestPart("files") List<MultipartFile> files, @PathVariable String context) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stream-file/{context}")
    public ResponseEntity<AssetUploadAssetFile> uploadStreamFile(HttpServletRequest request, @PathVariable String context) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/large-file")
    public ResponseEntity<Void> uploadLargeFile() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/{file-name}")
    public ResponseEntity<Void> fileExists(@PathVariable("file-name") String fileName) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{file-name}")
    public ResponseEntity<AssetDownloadStreamingFileResponseView> downloadStreamingFile(@PathVariable("file-name") String fileName) {
        return ResponseEntity.ok().build();
    }
}
