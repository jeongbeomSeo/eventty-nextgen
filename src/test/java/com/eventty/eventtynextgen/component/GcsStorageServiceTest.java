package com.eventty.eventtynextgen.component;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.component.StorageService.Purpose;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
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
@DisplayName("GcsImageStorageService 통합 테스트")
class GcsStorageServiceTest {

    @Autowired
    private GcsStorageService gcsImageStorageService;

    @Nested
    @DisplayName("이미지 파일 업로드 테스트")
    class UploadImageFile {

        @Test
        @DisplayName("jpg 이미지를 성공적으로 업로드한다.")
        void jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizeJpgImagePath = "src/test/resources/images/512KB_size_image.jpg";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("gif 이미지를 성공적으로 업로드한다.")
        void 용량이_작은_gif_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizeGifImagePath = "src/test/resources/images/657KB_size_image.gif";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizeGifImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("png 이미지를 성공적으로 업로드한다.")
        void png_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizePngImagePath = "src/test/resources/images/2MB_size_image.png";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizePngImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("svg 이미지를 성공적으로 업로드한다.")
        void svg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizeSvgImagePath = "src/test/resources/images/6KB_size_image.svg";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizeSvgImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("webp 이미지를 성공적으로 업로드한다.")
        void webp_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizeWebpImagePath = "src/test/resources/images/560KB_size_image.webp";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizeWebpImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("avif 이미지를 성공적으로 업로드한다.")
        void avif_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String lowerSizeAvifImagePath = "src/test/resources/images/40KB_size_image.avif";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(lowerSizeAvifImagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("크기가 2MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_2MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String imagePath = "src/test/resources/images/2MB_size_image.jpg";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(imagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("크기가 6MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_6MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String imagePath = "src/test/resources/images/6MB_size_image.jpg";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(imagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        @Test
        @DisplayName("크기가 17MB 용량인 jpg 이미지를 성공적으로 업로드한다.")
        void 크기가_17MB_용량인_jpg_이미지를_성공적으로_업로드한다() throws Exception {
            // given
            String imagePath = "src/test/resources/images/17MB_size_image.jpg";
            String imageContextType = ImageContextType.JPG.getType();

            MultipartFile imageFile = convertMultipartFile(imagePath, imageContextType);

            // when
            String imageUri = gcsImageStorageService.uploadFile(imageFile, Purpose.EVENT_IMAGE);

            // then
            assertThat(imageUri).isNotNull();
        }

        /**
         * <h3>순차 처리 방식 업로드 성능 결과</h3>
         * 목적: 이미지 파일 개수 및 크기에 따른 순차 업로드 성능 측정 및 정상 업로드 검증
         * <ul>
         *
         * <li> 5개(512KB): 약 1.6초</li>
         * <li> 20개(512KB): 약 3.7초</li>
         * <li> 5개(17MB): 약 7.8초</li>
         * <li> 20개(17MB): 약 27.7초</li>
         * </ul>
         */
        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 순차 처리 방식")
        class SequentiallyUploadImages {

            // 1 sec 628ms
            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드가 된다.")
            void 용량이_작은_5개의_이미지를_순차_처리_방식으로_업로드할_경우_모두_성공적으로_업로드가_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = List.of(imageFile, imageFile, imageFile, imageFile, imageFile);

                // when
                imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 3 sec 700ms
            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_순차_처리_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when
                imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 7 sec 824 ms
            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_순차_처리_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 5).mapToObj(i -> imageFile).toList();

                // when
                imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 18.173, 18.376, 18.196, 18.504, 17.968
            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 순차 처리 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_순차_처리_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String bigSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(bigSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when
                imageFiles.stream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }
        }

        /**
         * <h3>병렬 처리 방식 업로드 성능 결과</h3>
         * 목적: 이미지 파일 개수 및 크기에 따른 순차 업로드 성능 측정 및 정상 업로드 검증
         * <ul>
         *
         * <li> 5개(512KB): 약 1.3초</li>
         * <li> 20개(512KB): 약 2초</li>
         * <li> 5개(17MB): 약 8.3초</li>
         * <li> 20개(17MB): 약 26.6초</li>
         * </ul>
         */
        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 병렬 처리 방식")
        class ParallelUploadImages {

            // 1sec 341ms
            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 스트림 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_5개의_이미지를_스트림_병렬_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 5).mapToObj(i -> imageFile).toList();

                // when & then
                imageFiles.parallelStream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 1sec 984ms
            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 스트림 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_스트림_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when & then
                imageFiles.parallelStream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 8sec 334ms
            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String bigSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(bigSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 5).mapToObj(i -> imageFile).toList();

                // when & then
                imageFiles.parallelStream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 24.855, 23.606, 22.705, 22.557, 22.352
            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 병렬 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_병렬_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String bigSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(bigSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when & then
                imageFiles.parallelStream()
                    .map(file -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE))
                    .forEach(uri -> assertThat(uri).isNotNull());
            }
        }

        @Nested
        @DisplayName("[성능 분석] 여러 이미지 전송: 스트림 기반의 비동기 처리 방식")
        class AsynchronousUploadImages {

            // 1.226, 0.218, 0.231, 0,229, 0.249
            @RepeatedTest(5)
            @DisplayName("용량이 작은 5개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_5개의_이미지를_비동기_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 5).mapToObj(i -> imageFile).toList();

                // when
                List<CompletableFuture<String>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE), excutor))
                    .toList();

                // then
                futures
                    .stream().map(CompletableFuture::join)
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 1.470, 0.688, 0.817, 0.735, 0.650
            @RepeatedTest(5)
            @DisplayName("용량이 작은 20개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_작은_20개의_이미지를_비동기_방식으로_업로드_할_경우_모두_성공적으로_업로드_된다() throws Exception {
                // given
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "512KB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when
                List<CompletableFuture<String>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE), excutor))
                    .toList();

                // then
                futures
                    .stream().map(CompletableFuture::join)
                    .forEach(uri -> assertThat(uri).isNotNull());

            }

            // 1.836, 0.830, 0.847, 1.150, 0.938
            @RepeatedTest(5)
            @DisplayName("용량이 큰 1개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_1개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 1).mapToObj(i -> imageFile).toList();

                // when
                List<CompletableFuture<String>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE), excutor))
                    .toList();

                // then
                futures
                    .stream().map(CompletableFuture::join)
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // 4.218, 3.376, 3.315, 3.333, 3.870
            @RepeatedTest(5)
            @DisplayName("용량이 큰 5개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_5개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 5).mapToObj(i -> imageFile).toList();

                // when
                List<CompletableFuture<String>> futures = imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE), excutor))
                    .toList();

                // then
                futures
                    .stream().map(CompletableFuture::join)
                    .forEach(uri -> assertThat(uri).isNotNull());
            }

            // TODO: Java 프로파일링 툴 도입 + 모니터링 툴 도입 후 성능 분석 및 개선 시작
            // 왜 두방식은 차이가 없으면, 순차 방식과도 차이가 없는가? 병렬 방식은 도대체 스레드풀이 몇개 이길래 왜 차이가 없는가?
            // 확실하게 확인해야 될 것은 uploadFile의 동작 방식이다.
            // 24.360, 24.237, 22.540, 21.702, 23.273
            // 24.741, 23.700, 22.413, 22.455, 22.366

            // 16.509, 17.260, 16.482, 16.109, 16.115
            @RepeatedTest(5)
            @DisplayName("용량이 큰 20개의 이미지를 비동기 방식으로 업로드할 경우 모두 성공적으로 업로드 된다.")
            void 용량이_큰_20개의_이미지를_비동기_방식으로_업로드할_경우_모두_성공적으로_업로드_된다() throws Exception {
                ExecutorService excutor = Executors.newFixedThreadPool(10);
                String rootPath = "src/test/resources/images/";
                String imageContextType = ImageContextType.JPG.getType();

                String lowerSizeJpgImagePath = rootPath + "17MB_size_image.jpg";

                MultipartFile imageFile = convertMultipartFile(lowerSizeJpgImagePath, imageContextType);

                List<MultipartFile> imageFiles = IntStream.range(0, 20).mapToObj(i -> imageFile).toList();

                // when
                imageFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> gcsImageStorageService.uploadFile(file, Purpose.EVENT_IMAGE), excutor))
                    .map(CompletableFuture::join)
                    .forEach(uri -> assertThat(uri).isNotNull());
//                    .toList();

                // then
//                futures
//                    .stream().map(CompletableFuture::join)
//                    .forEach(uri -> assertThat(uri).isNotNull());
            }
        }
    }

    private MultipartFile convertMultipartFile(String filePath, String contextType) throws Exception {

        File file = new File(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return new MockMultipartFile(
                "file_name",
                file.getName(),
                contextType,
                fileInputStream);
        }
    }

    // TODO: convertMultipartFile 메서드와 무엇이 좋은지 비교
    private MultipartFile convertMultipartFileUsingFiles(String filePath, String contextType) throws Exception {
        Path path = java.nio.file.Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);

        return new MockMultipartFile(
            "file_name",
            path.getFileName().toString(),
            contextType,
            fileContent
        );
    }

    private enum ImageContextType {
        JPG("image/jpeg"),
        GIF("image/gif"),
        PNG("image/png"),
        SVG("image/svg+xml"),
        WEBP("image/webp"),
        AVIF("image/avif");

        private final String type;

        ImageContextType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}