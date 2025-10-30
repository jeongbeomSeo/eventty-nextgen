package com.eventty.eventtynextgen.asset.core;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsObjectStorageClient implements ObjectStorageClient {

    private final Storage storage;

    @Override
    public UploadFileMetaData uploadMultipartFile(MultipartFile file, String fileName, StorageContext context) {
        String ext = file.getContentType();
        String bucketName = getBucketInfo(context).getBucketName();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(ext)
            .build();

        try (WriteChannel writer = this.storage.writer(blobInfo)) {
            byte[] data = file.getBytes();
            writer.write(ByteBuffer.wrap(data));
        } catch (IOException e) {
            log.error("멀티파트 파일 업로드에 실패했습니다: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (StorageException e) {
            log.error("GCS 스토리지에 파일 업로드 중 오류가 발생했습니다: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("멀티파트 파일 업로드 중 예기치 못한 예외가 발생했습니다: {}", e.getMessage());
            throw e;
        }

        return new UploadFileMetaData(fileName, blobInfo.getContentType(), getFileUrl(getBucketInfo(context), fileName));
    }

    /**
     * 스트리밍 방식으로 파일을 업로드합니다.
     *
     * @deprecated 성능 및 안정성 문제로 인해 스트리밍 업로드 방식은 권장되지 않습니다. 대신 {@link #uploadMultipartFile(MultipartFile, String, StorageContext)} 멀티파트 파일 업로드 방식을 사용하세요.
     */
    @Deprecated
    @Override
    public UploadFileResult uploadStreaming(InputStream inputStream, StorageContext context, String contentType) {
        String fileName = UUID.randomUUID().toString();
        String bucketName = getBucketInfo(context).getBucketName();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType != null ? contentType : "application/octet-stream")
            .build();

        long totalBytes = 0L;

        try (WriteChannel writer = this.storage.writer(blobInfo);
            OutputStream out = Channels.newOutputStream(writer)) {
            totalBytes = inputStream.transferTo(out);
        } catch (IOException e) {
            log.error("Failed to upload streaming file caused: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return UploadFileResult.success(fileName, blobInfo.getContentType());
    }

    /**
     * 파일명 리스트에 해당하는 파일 URL들을 조회합니다.
     *
     * @deprecated 파일 메타데이터를 DB에 저장하므로 fileName을 통해 GCS로부터 Url을 조회하는 방식은 비효율적입니다.
     *             대신 DB에서 메타데이터를 조회하는 방식을 권장합니다.
     */
    @Deprecated
    @Override
    public String findFileUrl(String fileName, StorageContext context) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        List<String> fileNames = List.of(fileName);

        FindFileUrlResult result = findFileUrls(fileNames, context);

        return result.fileUrls().get(0);
    }

    /**
     * 파일명 리스트에 해당하는 파일 URL들을 조회합니다.
     *
     * @deprecated 파일 메타데이터를 DB에 저장하므로 fileName을 통해 GCS로부터 Url을 조회하는 방식은 비효율적입니다.
     *             대신 DB에서 메타데이터를 조회하는 방식을 권장합니다.
     */
    @Deprecated
    @Override
    public FindFileUrlResult findFileUrls(List<String> fileNames, StorageContext context) {
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("파일명 리스트가 null이거나 비어있을 수 없습니다");
        }

        if (fileNames.stream().anyMatch(fileName -> fileName == null || fileName.isBlank())) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        BucketInfo bucketInfo = getBucketInfo(context);
        String bucketName = bucketInfo.getBucketName();

        List<Blob> blobs = fileNames.stream()
            .map(fileName -> this.storage.get(BlobId.of(bucketName, fileName)))
            .filter(Objects::nonNull)
            .toList();

        if (blobs.isEmpty()) {
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILE);
        }

        Set<String> blobNameSet = blobs.stream()
            .map(BlobInfo::getName)
            .collect(Collectors.toSet());

        List<String> failedFileNames = fileNames.stream()
            .filter(fileName -> !blobNameSet.contains(fileName))
            .toList();

        List<String> fileUrls = blobs.stream()
            .map(blob -> bucketInfo.getBaseUrl() + blob.getName())
            .toList();

        return new FindFileUrlResult(fileUrls, failedFileNames);
    }

    @Override
    public String findFileDownloadLink(String fileName, StorageContext context) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        String bucketName = getBucketInfo(context).getBucketName();

        Blob blob = this.storage.get(BlobId.of(bucketName, fileName));

        if (blob == null) {
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILE);
        }

        return blob.getMediaLink();
    }

    @Override
    public boolean deleteFile(String fileName, StorageContext context) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        if (!existsFile(fileName, context)) {
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILE);
        }

        String bucketName = getBucketInfo(context).getBucketName();

        return this.storage.delete(BlobId.of(bucketName, fileName));
    }

    @Override
    public boolean existsFile(String fileName, StorageContext context) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }

        String bucketName = getBucketInfo(context).getBucketName();

        Blob blob = this.storage.get(BlobId.of(bucketName, fileName));

        return Optional.ofNullable(blob).map(Blob::exists).orElse(false);
    }

    private String getFileUrl(BucketInfo bucketInfo, String fileName) {
        return bucketInfo.getBaseUrl() + fileName;
    }

    private BucketInfo getBucketInfo(StorageContext context) {
        return BucketInfo.getBucketInfo(context).orElseThrow(
                () -> CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AssetErrorType.NOT_FOUND_BUCKET_NAME, "GcsImageStorageService.uploadImage"));
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum BucketInfo {

        EVENTTY_EVENT_IMAGE("eventty-event-image", StorageContext.EVENT_IMAGE, "https://storage.googleapis.com/eventty-event-image/"),
        EVENTTY_FILE("eventty-file", StorageContext.FILE, "https://storage.googleapis.com/eventty-file/");

        private final String bucketName;
        private final StorageContext context;
        private final String baseUrl;

        public static Optional<BucketInfo> getBucketInfo(StorageContext context) {
            for (BucketInfo bucketInfo : BucketInfo.values()) {
                if (bucketInfo.context == context) {
                    return Optional.of(bucketInfo);
                }
            }
            return Optional.empty();
        }
    }
}

