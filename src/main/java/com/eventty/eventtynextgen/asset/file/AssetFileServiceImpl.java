package com.eventty.eventtynextgen.asset.file;

import static com.eventty.eventtynextgen.base.exception.enums.AssetErrorType.FILE_UPLOAD_FAILED;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.shared.utils.RetryableUtils;
import jakarta.servlet.ServletInputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetFileServiceImpl implements AssetFileService {

    private final ObjectStorageClient objectStorageClient;
    private final FileMetaValidator fileMetaValidator;
    private final ThreadPoolTaskExecutor gcsIoExecutor;

    @Override
    public AssetUploadAssetFile uploadMultipartFile(MultipartFile file, String context) {

        VerifyResult verifyResult = this.fileMetaValidator.validateMultipartFile(file);
        handleVerifyResult(verifyResult);

        StorageContext contextEnum = StorageContext.getFileContext(context)
            .orElseThrow(() -> CustomException.badRequest(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT, "context: " + context));

        UploadFileResult uploadFileResult;
        try {
            uploadFileResult = RetryableUtils.executeWithRetry(() -> this.objectStorageClient.uploadMultipartFile(file, contextEnum), 3, 100, 1000);
        } catch (Exception e) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, FILE_UPLOAD_FAILED, "File upload interrupted: " + e.getMessage());
        }

        return new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentLength(), uploadFileResult.contentType());
    }

    @Override
    public List<AssetUploadAssetFile> uploadMultipartFiles(List<MultipartFile> files, String context) {
        files.stream().map(this.fileMetaValidator::validateMultipartFile).forEach(this::handleVerifyResult);

        StorageContext contextEnum = StorageContext.getFileContext(context)
            .orElseThrow(() -> CustomException.badRequest(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT, "context: " + context));

        // 아래 로직과 성능 테스트 진행 + 비동기 처리가 올바르게 되는지도 확인 + 재시도 패턴 적용
//        files.stream()
//            .map(file -> CompletableFuture.supplyAsync(() ->
//                this.objectStorageClient.uploadMultipartFile(file, contextEnum), this.gcsIoExecutor))
//            .map(CompletableFuture::join)
//            .toList();

        // TODO: 프로파일링을 통한 검증 이후 성능 테스트 진행 + 재시도 패턴 적용
        List<CompletableFuture<UploadFileResult>> futures = files.stream()
            .map(file -> CompletableFuture.supplyAsync(() ->
                this.objectStorageClient.uploadMultipartFile(file, contextEnum), this.gcsIoExecutor)
                .orTimeout(1000, TimeUnit.SECONDS))
            .toList();


        // TODO: 1. 실패시 전체 롤백, 2. 1개 실패시와 2개 이상 실패시 다른 예외 처리
//        futures.stream()
//            .map(CompletableFuture::join)
//            .map(uploadFileResult -> new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentLength(), uploadFileResult.contentType()))
//            .toList();

        List<UploadFileResult> results = futures.stream().map(future -> {
                try {
                    return future.join();
                } catch (Exception e) {
                    return UploadFileResult.fail(e);
                }
            })
            .toList();

        List<UploadFileResult> successUploadResults = results.stream()
            .filter(UploadFileResult::isSuccess)
            .toList();

        List<UploadFileResult> failedUploadResults = results.stream()
            .filter(result -> !result.isSuccess())
            .toList();

        if (!failedUploadResults.isEmpty()) {
            // 롤백 작업: 업로드에 성공한 파일 지우기
            successUploadResults.forEach(result -> this.objectStorageClient.deleteFile(result.fileName(), contextEnum));

            String failedMessage = failedUploadResults.stream().map(result -> result.exception().getMessage()).collect(Collectors.joining(", "));
            throw CustomException.badRequest(FILE_UPLOAD_FAILED, "File upload interrupted: " + failedMessage);
        }

        return results.stream()
            .map(result -> new AssetUploadAssetFile(result.fileName(), result.contentLength(), result.contentType()))
            .toList();
    }

    @Override
    public AssetUploadAssetFile uploadStreaming(ServletInputStream inputStream, String contentType, String context) {

        VerifyResult verifyResult = this.fileMetaValidator.validateStreamFile(contentType);
        handleVerifyResult(verifyResult);

        StorageContext contextEnum = StorageContext.getFileContext(context)
            .orElseThrow(() -> CustomException.badRequest(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT, "context: " + context));

        UploadFileResult uploadFileResult = this.objectStorageClient.uploadStreaming(inputStream, contextEnum, contentType);

        return new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentLength(), uploadFileResult.contentType());
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyFileMetaResult()) {
            case INVALID_SIZE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_SIZE, verifyResult.getDetails());
            case INVALID_CONTENT_TYPE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_CONTENT_TYPE, verifyResult.getDetails());
            case INVALID_EXTENSION -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_EXTENSION, verifyResult.getDetails());
        }
    }
}
