package com.eventty.eventtynextgen.asset.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileMetaData;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.utils.MultipartConvertHelper;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.FindFileUrlResult;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Tag("ExternalIntegration")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("GcsObjectStorageClient 통합 테스트")
class GcsObjectStorageClientTest {

    @Autowired
    private GcsObjectStorageClient gcsImageStorageService;

    @Nested
    @DisplayName("Multipart-File 방식 파일 업로드 테스트")
    class UploadMultipartFile {

        @Test
        @DisplayName("jpg 이미지를 성공적으로 업로드한다.")
        void jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("gif 이미지를 성공적으로 업로드한다.")
        void 용량이_작은_gif_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.gif", "image/gif", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("png 이미지를 성공적으로 업로드한다.")
        void png_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.png", "image/png", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("svg 이미지를 성공적으로 업로드한다.")
        void svg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.svg", "image/svg", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("webp 이미지를 성공적으로 업로드한다.")
        void webp_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.webp", "image/webp", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("avif 이미지를 성공적으로 업로드한다.")
        void avif_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.avif", "image/avif", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("크기가 2MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_2MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 2 * 1024 * 1024L;         // 2MB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("크기가 6MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_6MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 6 * 1024 * 1024L;         // 6MB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("크기가 17MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_17MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 17 * 1024 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            // when
            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(uploadFileMetaData.fileName()).isNotNull();
            assertThat(uploadFileMetaData.contentType()).isNotBlank();
            assertThat(uploadFileMetaData.fileUrl()).isNotBlank();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 순차 처리 방식")
        class SequentiallyUploadImages {

            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드가 된다.")
            void 용량이_작은_5개의_이미지를_순차_처리_방식으로_업로드할_경우_모두_성공적으로_업로드가_된다() throws Exception {
                // given
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_순차_처리_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_순차_처리_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_순차_처리_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }
        }

        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 병렬 처리 방식")
        class ParallelUploadImages {

            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 스트림 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_5개의_이미지를_스트림_병렬_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream().parallel()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 스트림 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_스트림_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream().parallel()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream().parallel()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<UploadFileMetaData> results = imageFiles.stream().parallel()
                    .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }
        }

        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 비동기 처리 방식")
        class AsynchronousUploadImages {

            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_5개의_이미지를_비동기_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<CompletableFuture<UploadFileMetaData>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(
                        () -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE), excutor))
                    .toList();

                List<UploadFileMetaData> results = futures
                    .stream().map(CompletableFuture::join)
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_비동기_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                long sizeInBytes = 512 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<CompletableFuture<UploadFileMetaData>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(
                        () -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE), excutor))
                    .toList();

                List<UploadFileMetaData> results = futures
                    .stream().map(CompletableFuture::join)
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 1개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_1개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<CompletableFuture<UploadFileMetaData>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(
                        () -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE), excutor))
                    .toList();

                List<UploadFileMetaData> results = futures
                    .stream().map(CompletableFuture::join)
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<CompletableFuture<UploadFileMetaData>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(
                        () -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE), excutor))
                    .toList();

                List<UploadFileMetaData> results = futures
                    .stream().map(CompletableFuture::join)
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }

            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                long sizeInBytes = 17 * 1024 * 1024L;
                byte[] content = new byte[(int) sizeInBytes];
                List<MockMultipartFile> imageFiles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
                }

                // when
                List<CompletableFuture<UploadFileMetaData>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(
                        () -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE), excutor))
                    .toList();

                List<UploadFileMetaData> results = futures
                    .stream().map(CompletableFuture::join)
                    .toList();

                // then
                results.forEach(result -> {
                    assertThat(result.fileName()).isNotNull();
                    assertThat(result.contentType()).isNotBlank();
                    assertThat(result.fileUrl()).isNotBlank();
                });

                results.forEach(result -> gcsImageStorageService.deleteFile(result.fileName(), StorageContext.EVENT_IMAGE));
            }
        }
    }

    @Nested
    @DisplayName("파일 유무 확인 테스트")
    class FileExistsTest {
        @Test
        @DisplayName("존재하는 파일인 경우 true를 반환한다")
        void 존재하는_파일인_경우_true를_반환한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // when
            boolean fileExists = gcsImageStorageService.existsFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);

            // then
            assertThat(fileExists).isTrue();

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("존재하지 않는 파일인 경우 false를 반환한다")
        void 존재하지_않는_파일인_경우_false를_반환한다() throws Exception {
            // given
            String fileUrl = "not_exist_file_name";

            // when
            boolean fileExists = gcsImageStorageService.existsFile(fileUrl, StorageContext.EVENT_IMAGE);

            // then
            assertThat(fileExists).isFalse();
        }

        @Test
        @DisplayName("파일명이 빈 값인 경우 false를 반환한다")
        void 파일명이_빈_값인_경우_false를_반환한다() throws Exception {
            // given
            String fileUrl = "";

            // when
            boolean fileExists = gcsImageStorageService.existsFile(fileUrl, StorageContext.EVENT_IMAGE);

            // then
            assertThat(fileExists).isFalse();
        }
    }

    @Disabled("Deprecated된 API입니다.")
    @Nested
    @DisplayName("단일 파일 URL 조회 테스트")
    class FindFileUrlTest {
        @Test
        @DisplayName("1개의 파일 Name을 인자로 받아 공개 File Url를 조회하여 반환한다")
        void 단일_파일_Name을__인자로_받아_공개_File_Url를_조회하여_반환한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            UploadFileMetaData uploadFileMetaData = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE);

            // when
            String fileUrl = gcsImageStorageService.findFileUrl(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);

            System.out.println(fileUrl);

            // then
            assertThat(fileUrl).isNotNull();
            assertThat(fileUrl).startsWith("https://storage.googleapis.com");

            gcsImageStorageService.deleteFile(uploadFileMetaData.fileName(), StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("fileName이 null일 경우 예외를 발생시킨다")
        void fileName이_null일_경우_예외를_발생시킨다() {
            // given
            String fileName = null;

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrl(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("fileName이 빈 값일 경우 예외를 발생시킨다")
        void fileName이_빈_값일_경우_예외를_발생시킨다() {
            // given
            String fileName = "";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrl(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("fileName을 통해 파일을 찾을 수 없는 경우 예외를 발생시킨다")
        void fileName을_통해_파일을_찾을_수_없는_경우_예외를_발생시킨다() {
            // given
            String fileName = "not_exist_file_name";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrl(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.NOT_FOUND_FILES);
                });
        }
    }

    @Disabled("Deprecated된 API입니다.")
    @Nested
    @DisplayName("여러 파일 URL 조회 테스트")
    class FindFileUrlsTest {
        @Test
        @DisplayName("여러 개의 파일 Name을 인자로 받아 공개 File Urls를 조회하여 반환한다")
        void 여러_개의_파일_Name을_인자로_받아_공개_File_Urls를_조회하여_반환한다() throws Exception {
            // given
            long sizeInBytes = 512 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            List<MockMultipartFile> imageFiles = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
            }

            List<String> fileNames = imageFiles.stream()
                .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                .map(UploadFileMetaData::fileName)
                .toList();

            // when
            FindFileUrlResult result = gcsImageStorageService.findFileUrls(fileNames, StorageContext.EVENT_IMAGE);

            // then
            assertThat(result.fileUrls().size()).isEqualTo(5);
            assertThat(result.failedFileNames()).isEmpty();
            result.fileUrls().forEach(url ->
                assertThat(url).startsWith("https://storage.googleapis.com"));

            fileNames.forEach(fileName -> {
                try {
                    gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE);
                } catch (Exception ignored) {}
            });
        }

        @Test
        @DisplayName("여러 개의 fileName 중 하나라도 null이 포함되어 있으면 예외를 발생시킨다")
        void 여러_개의_fileName_중_하나라도_null이_포함되어_있으면_예외를_발생시킨다() throws Exception {
            // given
            long sizeInBytes = 512 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            List<MockMultipartFile> imageFiles = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
            }

            List<String> fileNames = imageFiles.stream()
                .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                .map(UploadFileMetaData::fileName)
                .toList();

            List<String> list = new ArrayList<>(fileNames);
            list.add(null);

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrls(list, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);

            fileNames.forEach(fileName -> {
                try {
                    gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE);
                } catch (Exception ignored) {}
            });
        }

        @Test
        @DisplayName("여러 개의 fileName 중 하나라도 빈 값이 포함되어 있으면 예외를 발생시킨다")
        void 여러_개의_fileName_중_하나라도_빈_값이_포함되어_있으면_예외를_발생시킨다() throws Exception {
            // given
            long sizeInBytes = 512 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            List<MockMultipartFile> imageFiles = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
            }

            List<String> fileNames = imageFiles.stream()
                .map(file -> gcsImageStorageService.uploadMultipartFile(file, file.getName(), StorageContext.EVENT_IMAGE))
                .map(UploadFileMetaData::fileName)
                .toList();

            List<String> list = new ArrayList<>(fileNames);
            list.add("");

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrls(list, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);

            fileNames.forEach(fileName -> {
                try {
                    gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE);
                } catch (Exception ignored) {}
            });
        }

        @Test
        @DisplayName("존재하는 파일 Name에 대해서는 공개 URL을 반환하고, 존재하지 않는 파일 Name은 별도의 필드에 리스트로 반환한다.")
        void 존재하는_파일_Name에_대해서는_공개_URL을_반환하고_존재하지_않는_파일_Name은_별도의_필드에_리스트로_반환한다() throws Exception {
            // given
            long sizeInBytes = 512 * 1024L;
            byte[] content = new byte[(int) sizeInBytes];
            List<MockMultipartFile> imageFiles = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                imageFiles.add(new MockMultipartFile("테스트_파일_이름" + i, "image.jpg", "image/jpeg", content));
            }

            List<String> fileNames = imageFiles.stream()
                .map(file -> gcsImageStorageService.uploadMultipartFile(file, UUID.randomUUID().toString(), StorageContext.EVENT_IMAGE))
                .map(UploadFileMetaData::fileName)
                .toList();

            List<String> list = new ArrayList<>(fileNames);
            list.add("not_exist_file_name1");
            list.add("not_exist_file_name2");

            // when
            FindFileUrlResult result = gcsImageStorageService.findFileUrls(list, StorageContext.EVENT_IMAGE);

            // then
            assertThat(result.fileUrls().size()).isEqualTo(3);
            assertThat(result.failedFileNames().size()).isEqualTo(2);
            assertThat(result.failedFileNames()).contains("not_exist_file_name1", "not_exist_file_name2");
            result.fileUrls().forEach(url ->
                assertThat(url).startsWith("https://storage.googleapis.com"));

            fileNames.forEach(fileName -> {
                try {
                    gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE);
                } catch (Exception ignored) {}
            });
        }

        @Test
        @DisplayName("모든 파일이 존재하지 않는 경우에는 예외를 발생시킨다")
        void 모든_파일이_존재하지_않는_경우에는_예외를_발생시킨다() throws Exception {
            // given
            List<String> fileNames = List.of("not_exist_file_name1", "not_exist_file_name2", "not_exist_file_name2");

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileUrls(fileNames, StorageContext.EVENT_IMAGE))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.NOT_FOUND_FILES);
                });
        }
    }

    @Nested
    @DisplayName("파일 다운로드 링크 조회 테스트")
    class FindFileDownloadLinkTest {
        @Test
        @DisplayName("파일 이름을 인자로 받아 다운로드 링크를 조회하여 반환한다")
        void 파일_이름을_인자로_받아_다운로드_링크를_조회하여_반환한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            String fileFullName = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE).fileName();

            // when
            String downloadLink = gcsImageStorageService.findFileDownloadLink(fileFullName, StorageContext.EVENT_IMAGE);

            System.out.println(downloadLink);

            // then
            assertThat(downloadLink).isNotNull();
            assertThat(downloadLink).startsWith("https://storage.googleapis.com");
            assertThat(downloadLink).contains("download");

            gcsImageStorageService.deleteFile(fileFullName, StorageContext.EVENT_IMAGE);
        }

        @Test
        @DisplayName("파일 이름을 통해 파일을 찾지 못한 경우 예외를 발생시킨다")
        void 파일_이름을_통해_파일을_찾지_못한_경우_예외를_발생시킨다() {
            // given
            String fileName = "not_exist_file_name";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileDownloadLink(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.NOT_FOUND_FILES);
                });
        }

        @Test
        @DisplayName("파일 이름 인자값이 null일 경우 예외를 밝생시킨다")
        void 파일_이름_인자값이_null일_경우_예외를_밝생시킨다() {
            // given
            String fileName = null;

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileDownloadLink(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("파일 이름 인자값이 공백일 경우 예외를 발생시킨다")
        void 파일_이름_인자값이_공백일_경우_예외를_발생시킨다() {
            // given
            String fileName = "";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.findFileDownloadLink(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("파일 삭제 테스트")
    class DeleteFileTest {
        @Test
        @DisplayName("파일 이름을 인자로 받아 파일을 삭제한다")
        void 파일_이름을_인자로_받아_파일을_삭제한다() throws Exception {
            // given
            String fileName = "테스트_파일_이름";

            long sizeInBytes = 512 * 1024L;         // 512KB
            byte[] content = new byte[(int) sizeInBytes];
            MockMultipartFile imageFile = new MockMultipartFile(fileName, "image.jpg", "image/jpeg", content);

            String fileFullName = gcsImageStorageService.uploadMultipartFile(imageFile, fileName, StorageContext.EVENT_IMAGE).fileName();

            // when
            boolean result = gcsImageStorageService.deleteFile(fileFullName, StorageContext.EVENT_IMAGE);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("파일 이름을 통해 파일을 찾지 못한 경우 예외를 발생시킨다")
        void 파일_이름을_통해_파일을_찾지_못한_경우_예외를_발생시킨다() {
            // given
            String fileName = "not_exist_file_name";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.NOT_FOUND_FILES);
                });
        }

        @Test
        @DisplayName("파일 이름이 null일 경우 예외를 발생시킨다")
        void 파일_이름이_null일_경우_예외를_발생시킨다() {
            // given
            String fileName = null;

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("파일 이름이 빈 값일 경우 예외를 발생시킨다")
        void 파일_이름이_빈_값일_경우_예외를_발생시킨다() {
            // given
            String fileName = "";

            // when & then
            assertThatThrownBy(() -> gcsImageStorageService.deleteFile(fileName, StorageContext.EVENT_IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}