package com.eventty.eventtynextgen.asset.core;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
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
    public UploadFileResult uploadMultipartFile(MultipartFile file, Context context) {
        String fileName = UUID.randomUUID().toString();
        String ext = file.getContentType();
        String bucketName = getBucketInfo(context).getBucketName();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(ext)
            .build();

        try (WriteChannel writer = this.storage.writer(blobInfo)) {
            byte[] imageData = file.getBytes();
            writer.write(ByteBuffer.wrap(imageData));
        } catch (IOException e) {
            log.error("Failed to upload multipart file caused: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return new UploadFileResult(fileName, blobInfo.getSize(), blobInfo.getContentType());
    }

    @Override
    public UploadFileResult uploadStreaming(InputStream inputStream, Context context, String contentType) {
        String fileName = UUID.randomUUID().toString();
        String bucketName = getBucketInfo(context).getBucketName();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType != null ? contentType : "application/octet-stream")
            .build();

        long totalBytes = 0l;

        try (WriteChannel writer = this.storage.writer(blobInfo);
            OutputStream out = Channels.newOutputStream(writer)) {
            totalBytes = inputStream.transferTo(out);
        } catch (IOException e) {
            log.error("Failed to upload streaming file caused: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return new UploadFileResult(fileName, totalBytes, blobInfo.getContentType());
    }

    @Override
    public String findFileUrl(String fileName, Context context) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        List<String> fileNames = List.of(fileName);

        FindFileUrlResult result = findFileUrls(fileNames, context);

        return result.fileUrls().get(0);
    }

    // TODO: 파일 조회 실패시 구체적인 예외를 받아올 수 있도록 추후에 수정
    @Override
    public FindFileUrlResult findFileUrls(List<String> fileNames, Context context) {
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
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILES);
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
    public String findFileDownloadLink(String fileName, Context context) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        String bucketName = getBucketInfo(context).getBucketName();

        Blob blob = this.storage.get(BlobId.of(bucketName, fileName));

        if (blob == null) {
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILES);
        }

        return blob.getMediaLink();
    }

    @Override
    public boolean deleteFile(String fileName, Context context) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        if (!existsFile(fileName, context)) {
            throw CustomException.badRequest(AssetErrorType.NOT_FOUND_FILES);
        }

        String bucketName = getBucketInfo(context).getBucketName();

        return this.storage.delete(BlobId.of(bucketName, fileName));
    }

    @Override
    public boolean existsFile(String fileName, Context context) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }

        String bucketName = getBucketInfo(context).getBucketName();

        Blob blob = this.storage.get(BlobId.of(bucketName, fileName));

        return Optional.ofNullable(blob).map(Blob::exists).orElse(false);
    }

    private BucketInfo getBucketInfo(Context context) {
        return BucketInfo.getBucketInfo(context).orElseThrow(
                () -> CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AssetErrorType.NOT_FOUND_BUCKET_NAME, "GcsImageStorageService.uploadImage"));

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum BucketInfo {

        EVENTTY_EVENT_IMAGE("eventty-event-image", Context.EVENT_IMAGE, "https://storage.googleapis.com/eventty-event-image/");

        private final String bucketName;
        private final Context context;
        private final String baseUrl;

        public static Optional<BucketInfo> getBucketInfo(Context context) {
            for (BucketInfo bucketInfo : BucketInfo.values()) {
                if (bucketInfo.context == context) {
                    return Optional.of(bucketInfo);
                }
            }
            return Optional.empty();
        }
    }
}

