package com.eventty.eventtynextgen.asset.file;

import static com.eventty.eventtynextgen.base.exception.enums.AssetErrorType.FILE_UPLOAD_FAILED;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileMetaData;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.asset.file.service.FileMetadataService;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
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

    private final FileMetadataService fileMetadataService;
    private final ObjectStorageClient objectStorageClient;
    private final FileMetaValidator fileMetaValidator;
    private final ThreadPoolTaskExecutor gcsIoExecutor;

    @Override
    public AssetUploadAssetFile uploadMultipartFile(MultipartFile file, Long userId, String fileName) {

        VerifyResult verifyResult = this.fileMetaValidator.validateMultipartFile(file);
        handleVerifyResult(verifyResult);

        String fileFullName = createFileFullName(userId, fileName);

        // TODO: Retry 패턴 유틸리티 객체 수정 후 적용
        UploadFileMetaData uploadFileMetaData = this.objectStorageClient.uploadMultipartFile(file, fileFullName, StorageContext.FILE);

        FileMetadata fileMetadataFromDb = fileMetadataService.save(userId, uploadFileMetaData.fileName(), uploadFileMetaData.contentType(),
            uploadFileMetaData.fileSize(),
            uploadFileMetaData.fileUrl());

        return new AssetUploadAssetFile(fileMetadataFromDb.getId(), fileMetadataFromDb.getFileName(), fileMetadataFromDb.getContentType(),
            fileMetadataFromDb.getFileSize(), fileMetadataFromDb.getFileUrl());
    }

    private String createFileFullName(Long userId, String fileName) {
        return userId + "/" + fileName;
    }

    @Deprecated
    @Override
    public List<AssetUploadAssetFile> uploadMultipartFiles(List<MultipartFile> files) {
        files.stream().map(this.fileMetaValidator::validateMultipartFile).forEach(this::handleVerifyResult);

        List<CompletableFuture<UploadFileResult>> futures = files.stream()
            .map(file -> CompletableFuture.supplyAsync(() -> {
                UploadFileMetaData uploadFileMetaData = this.objectStorageClient.uploadMultipartFile(file, UUID.randomUUID().toString(), StorageContext.FILE);
                return UploadFileResult.success(uploadFileMetaData.fileName(), uploadFileMetaData.contentType());
            }, this.gcsIoExecutor).orTimeout(1000, TimeUnit.SECONDS))
                .toList();

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
            successUploadResults.forEach(result -> this.objectStorageClient.deleteFile(result.fileName(), StorageContext.FILE));

            String failedMessage = failedUploadResults.stream().map(result -> result.exception().getMessage()).collect(Collectors.joining(", "));
            throw CustomException.badRequest(FILE_UPLOAD_FAILED, "File upload interrupted: " + failedMessage);
        }

        return results.stream()
            .map(result -> new AssetUploadAssetFile(null, result.fileName(), result.contentType(), null, null))
            .toList();
    }

    @Override
    public AssetUploadAssetFile uploadStreaming(HttpServletRequest request) {
        String contentType = request.getContentType();
        ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException ex) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorType.OCCURRED_IO_EXCEPTION, "Message: " + ex.getMessage());
        }

        VerifyResult verifyResult = this.fileMetaValidator.validateStreamFile(contentType);
        handleVerifyResult(verifyResult);

        UploadFileResult uploadFileResult;
        try {
            uploadFileResult = this.objectStorageClient.uploadStreaming(inputStream, StorageContext.FILE, contentType);
        } catch (Exception e) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, FILE_UPLOAD_FAILED, "File upload interrupted: " + e.getMessage());
        }

        return new AssetUploadAssetFile(uploadFileResult.fileName(), uploadFileResult.contentType());
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyFileMetaResult()) {
            case INVALID_SIZE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_SIZE, verifyResult.getDetails());
            case INVALID_CONTENT_TYPE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_CONTENT_TYPE, verifyResult.getDetails());
            case INVALID_EXTENSION -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_EXTENSION, verifyResult.getDetails());
        }
    }
}
