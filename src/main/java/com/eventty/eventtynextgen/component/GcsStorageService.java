package com.eventty.eventtynextgen.component;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.StorageErrorType;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.nio.ByteBuffer;
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
public class GcsStorageService implements StorageService {

    private final Storage storage;

    @Override
    public String uploadFile(MultipartFile file, Purpose purpose) {
        String fileName = UUID.randomUUID().toString();
        String ext = file.getContentType();
        String bucketName = getBucketInfo(purpose).getBucketName();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(ext)
            .build();

        try (WriteChannel writer = this.storage.writer(blobInfo)) {
            byte[] imageData = file.getBytes();
            writer.write(ByteBuffer.wrap(imageData));
        } catch (IOException e) {
            log.error("Failed to upload image caused: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return blobInfo.getName();
    }

    @Override
    public String findFileUrl(String fileName, Purpose purpose) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        List<String> fileNames = List.of(fileName);

        FindFileUrlResult result = findFileUrls(fileNames, purpose);

        return result.fileUrls().get(0);
    }

    // TODO: 파일 조회 실패시 구체적인 예외를 받아올 수 있도록 추후에 수정
    @Override
    public FindFileUrlResult findFileUrls(List<String> fileNames, Purpose purpose) {
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("파일명 리스트가 null이거나 비어있을 수 없습니다");
        }

        if (fileNames.stream().anyMatch(fileName -> fileName == null || fileName.isBlank())) {
            throw new IllegalArgumentException("파일명이 null이거나 공백일 수 없습니다");
        }

        BucketInfo bucketInfo = getBucketInfo(purpose);
        String bucketName = bucketInfo.getBucketName();

        List<Blob> blobs = fileNames.stream()
            .map(fileName -> this.storage.get(BlobId.of(bucketName, fileName)))
            .filter(Objects::nonNull)
            .toList();

        if (blobs.isEmpty()) {
            throw CustomException.badRequest(StorageErrorType.NOT_FOUND_FILES);
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
    public byte[] downloadFile(String fileUrl) {
        return new byte[0];
    }

    @Override
    public void deleteFile(String fileUrl) {

    }

    @Override
    public boolean existsFile(String fileName, Purpose purpose) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }

        String bucketName = getBucketInfo(purpose).getBucketName();

        Blob blob = this.storage.get(BlobId.of(bucketName, fileName));

        return Optional.ofNullable(blob).map(Blob::exists).orElse(false);
    }

    private BucketInfo getBucketInfo(Purpose purpose) {
        return BucketInfo.getBucketInfo(purpose).orElseThrow(
                () -> CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, StorageErrorType.NOT_FOUND_BUCKET_NAME, "GcsImageStorageService.uploadImage"));

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum BucketInfo {

        EVENTTY_EVENT_IMAGE("eventty-event-image", Purpose.EVENT_IMAGE, "https://storage.googleapis.com/eventty-event-image/");

        private final String bucketName;
        private final Purpose purpose;
        private final String baseUrl;

        public static Optional<BucketInfo> getBucketInfo(Purpose purpose) {
            for (BucketInfo bucketInfo : BucketInfo.values()) {
                if (bucketInfo.purpose == purpose) {
                    return Optional.of(bucketInfo);
                }
            }
            return Optional.empty();
        }
    }
}

