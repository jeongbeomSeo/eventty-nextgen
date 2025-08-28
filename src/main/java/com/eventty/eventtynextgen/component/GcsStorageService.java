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
import java.util.Optional;
import java.util.UUID;
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
        String bucketName = getBucketName(purpose);

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(ext)
            .build();

        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] imageData = file.getBytes();
            writer.write(ByteBuffer.wrap(imageData));
        } catch (IOException e) {
            log.error("Failed to upload image caused: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return blobInfo.getName();
    }

    @Override
    public void deleteFile(String fileUrl) {

    }

    @Override
    public byte[] downloadFile(String fileUrl) {
        return new byte[0];
    }

    @Override
    public boolean fileExists(String fileUrl, Purpose purpose) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        String bucketName = getBucketName(purpose);

        Blob blob = storage.get(BlobId.of(bucketName, fileUrl));

        return Optional.ofNullable(blob).map(Blob::exists).orElse(false);
    }

    private String getBucketName(Purpose purpose) {
        return BucketName.getBucketName(purpose.name()).orElseThrow(
                () -> CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, StorageErrorType.NOT_FOUND_BUCKET_NAME, "GcsImageStorageService.uploadImage"))
            .getBucketName();
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum BucketName {

        EVENTTY_EVENT_IMAGE("eventty-event-image", "event_image");

        private final String bucketName;
        private final String purpose;

        public static Optional<BucketName> getBucketName(String purpose) {
            String purposeLowerCase = purpose.toLowerCase();
            for (BucketName bucketName : BucketName.values()) {
                if (bucketName.purpose.equals(purposeLowerCase)) {
                    return Optional.of(bucketName);
                }
            }
            return Optional.empty();
        }
    }
}

