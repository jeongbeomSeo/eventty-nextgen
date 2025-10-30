package com.eventty.eventtynextgen.asset.file;

import static com.eventty.eventtynextgen.base.exception.enums.AssetErrorType.FILE_UPLOAD_FAILED;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileMetaData;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetadataValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetadataValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.response.AssetDeleteFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetDownloadFileResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetFindFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFileResponseView;
import com.eventty.eventtynextgen.asset.file.service.FileMetadataService;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
    private final FileMetadataValidator fileMetadataValidator;
    private final ThreadPoolTaskExecutor gcsIoExecutor;

    @Override
    public AssetUploadAssetFileResponseView uploadMultipartFile(MultipartFile file, Long userId, String fileName) {

        VerifyResult verifyResult = this.fileMetadataValidator.validateMultipartFile(file);
        handleVerifyResult(verifyResult);

        String fileFullName = createFileFullName(userId, fileName);

        // TODO: Retry 패턴 유틸리티 객체 수정 후 적용
        UploadFileMetaData uploadFileMetaData = this.objectStorageClient.uploadMultipartFile(file, fileFullName, StorageContext.FILE);

        FileMetadata fileMetadataFromDb = fileMetadataService.save(userId, uploadFileMetaData.fileName(), uploadFileMetaData.contentType(), uploadFileMetaData.fileUrl());

        return new AssetUploadAssetFileResponseView(fileMetadataFromDb.getId(), fileMetadataFromDb.getFileName(), fileMetadataFromDb.getContentType(), fileMetadataFromDb.getFileUrl());
    }

    private String createFileFullName(Long userId, String fileName) {
        return userId + "/" + fileName;
    }

    @Deprecated
    @Override
    public List<AssetUploadAssetFileResponseView> uploadMultipartFiles(List<MultipartFile> files) {
        files.stream().map(this.fileMetadataValidator::validateMultipartFile).forEach(this::handleVerifyResult);

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
            .map(result -> new AssetUploadAssetFileResponseView(null, result.fileName(), result.contentType(), null))
            .toList();
    }

    @Deprecated
    @Override
    public AssetUploadAssetFileResponseView uploadStreaming(HttpServletRequest request) {
        String contentType = request.getContentType();
        ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException ex) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorType.OCCURRED_IO_EXCEPTION, "Message: " + ex.getMessage());
        }

        VerifyResult verifyResult = this.fileMetadataValidator.validateStreamFile(contentType);
        handleVerifyResult(verifyResult);

        UploadFileResult uploadFileResult;
        try {
            uploadFileResult = this.objectStorageClient.uploadStreaming(inputStream, StorageContext.FILE, contentType);
        } catch (Exception e) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, FILE_UPLOAD_FAILED, "File upload interrupted: " + e.getMessage());
        }

        return new AssetUploadAssetFileResponseView(null, null, null, null);
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyFileMetaResult()) {
            case INVALID_SIZE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_SIZE, verifyResult.getDetails());
            case INVALID_CONTENT_TYPE -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_CONTENT_TYPE, verifyResult.getDetails());
            case INVALID_EXTENSION -> throw CustomException.badRequest(AssetErrorType.INVALID_FILE_EXTENSION, verifyResult.getDetails());
        }
    }

    @Override
    public AssetGetFileMetadataResponseView getFileMetadata(Long userId, Long fileMetadataId) {
        FileMetadata fileMetadata = fileMetadataService.findById(fileMetadataId);

        if (!fileMetadata.getUserId().equals(userId)) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.UNAUTHORIZED_FILE_ACCESS);
        }

        if (fileMetadata.isDeleted()) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.NOT_ALLOW_ACCESS_DELETED_FILE);
        }

        return new AssetGetFileMetadataResponseView(fileMetadata.getId(), fileMetadata.getFileName(), fileMetadata.getContentType(), fileMetadata.getFileUrl());
    }

    @Override
    public AssetFindFileMetadataResponseView findFileMetadata(Long userId) {
        List<AssetFindFileMetadataResponseView.FileMetadata> fileMetaDatas = fileMetadataService.findAllByUserId(userId).stream()
            .filter(fileMetadata -> !fileMetadata.isDeleted())
            .map(fileMetadata ->
            new AssetFindFileMetadataResponseView.FileMetadata(
                fileMetadata.getId(),
                fileMetadata.getFileName(),
                fileMetadata.getContentType(),
                fileMetadata.getFileUrl())
        ).toList();

        return new AssetFindFileMetadataResponseView(userId, fileMetaDatas);
    }

    @Override
    public AssetDownloadFileResponseView downloadFile(Long userId, Long fileMetadataId) {

        FileMetadata fileMetadata = fileMetadataService.findById(fileMetadataId);

        if (!Objects.equals(userId, fileMetadata.getUserId())) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.UNAUTHORIZED_FILE_ACCESS);
        }

        if (fileMetadata.isDeleted()) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.NOT_ALLOW_ACCESS_DELETED_FILE);
        }

        String downloadLink = objectStorageClient.findFileDownloadLink(fileMetadata.getOriginFileName(), StorageContext.FILE);

        return new AssetDownloadFileResponseView(fileMetadata.getId(), fileMetadata.getFileName(), fileMetadata.getContentType(), downloadLink);
    }

    @Override
    public AssetDeleteFileResponseView deleteFile(Long userId, Long fileMetadataId) {

        FileMetadata fileMetadata = fileMetadataService.findById(fileMetadataId);

        if (!Objects.equals(userId, fileMetadata.getUserId())) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.UNAUTHORIZED_FILE_ACCESS);
        }

        if (fileMetadata.isDeleted()) {
            throw CustomException.of(HttpStatus.FORBIDDEN, AssetErrorType.ALREADY_FILE_DELETED);
        }

        boolean deleted = this.objectStorageClient.deleteFile(fileMetadata.getOriginFileName(), StorageContext.FILE);

        if (!deleted) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AssetErrorType.FAIL_GCS_FILE_DELETE, "userId: " + userId + ", fileMetadataId: " + fileMetadataId);
        }

        this.fileMetadataService.deleteById(fileMetadata.getId());

        return new AssetDeleteFileResponseView(fileMetadata.getId());
    }
}
