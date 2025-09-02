package com.eventty.eventtynextgen.asset.file;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.Context;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import jakarta.servlet.ServletInputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AssetFileServiceImpl implements AssetFileService {

    private final ObjectStorageClient objectStorageClient;
    private final FileMetaValidator fileMetaValidator;
    private final ThreadPoolTaskExecutor gcsIoExecutor;

    @Override
    public AssetUploadAssetFile uploadMultipartFile(MultipartFile file, String fileContext) {

        VerifyResult verifyResult = this.fileMetaValidator.validateFile(file);
        handleVerifyResult(verifyResult);

        Context contextEnum = Context.getFileContext(fileContext)
            .orElseThrow(() -> CustomException.badRequest(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT, "context: " + fileContext));

        UploadFileResult uploadFileResult = this.objectStorageClient.uploadMultipartFile(file, contextEnum);

        return new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentLength(), uploadFileResult.contentType());
    }

    @Override
    public List<AssetUploadAssetFile> uploadMultipartFiles(List<MultipartFile> files, String context) {

        files.stream().map(this.fileMetaValidator::validateFile).forEach(this::handleVerifyResult);

        Context contextEnum = Context.getFileContext(context)
            .orElseThrow(() -> CustomException.badRequest(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT, "context: " + context));

        // 아래 로직과 성능 테스트 진행 + 비동기 처리가 올바르게 되는지도 확인 + 재시도 패턴 적용
//        files.stream()
//            .map(file -> CompletableFuture.supplyAsync(() ->
//                this.objectStorageClient.uploadMultipartFile(file, contextEnum), this.gcsIoExecutor))
//            .map(CompletableFuture::join)
//            .toList();

        List<CompletableFuture<UploadFileResult>> futures = files.stream()
            .map(file -> CompletableFuture.supplyAsync(() ->
                this.objectStorageClient.uploadMultipartFile(file, contextEnum), this.gcsIoExecutor))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)
            .map(uploadFileResult -> new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentLength(), uploadFileResult.contentType()))
            .toList();
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyFileMetaResult()) {
            case INVALID_SIZE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_SIZE, verifyResult.getDetails());
            case INVALID_CONTENT_TYPE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_CONTENT_TYPE, verifyResult.getDetails());
            case INVALID_EXTENSION -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_EXTENSION, verifyResult.getDetails());
        }
    }

    @Override
    public AssetUploadAssetFile uploadStreaming(ServletInputStream inputStream, String context) {
        return null;
    }
}
