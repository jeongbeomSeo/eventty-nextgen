package com.eventty.eventtynextgen.asset.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.StorageContext;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileMetaData;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetadataValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetadataValidator.VerifyFileMetaResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetadataValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.entity.FileMetadata;
import com.eventty.eventtynextgen.asset.file.response.AssetFindFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetGetFileMetadataResponseView;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.asset.file.service.FileMetadataService;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssetFileServiceImpl 단위 테스트")
class AssetFileServiceImplTest {

    @Mock
    private FileMetadataService fileMetadataService;

    @Mock
    private ObjectStorageClient objectStorageClient;

    @Mock
    private FileMetadataValidator fileMetaValidator;

    private ThreadPoolTaskExecutor gcsIoExecutor;

    @BeforeEach
    void setUp() {
        gcsIoExecutor = new ThreadPoolTaskExecutor();
        gcsIoExecutor.setCorePoolSize(2);
        gcsIoExecutor.setMaxPoolSize(4);
        gcsIoExecutor.setQueueCapacity(100);
        gcsIoExecutor.setThreadNamePrefix("test-gcs-");
        gcsIoExecutor.initialize();
    }

    @Nested
    @DisplayName("MultipartFile 업로드 테스트")
    class UploadMultipartFile {

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 성공하고 파일 업로드를 수행하여 정상적으로 AssetUploadAssetFile을 반환한다")
        void 메타_데이터의_유효성_검증에_성공하고_파일_업로드를_수행하여_정상적으로_AssetUploadAssetFile을_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            Long userId = 1L;
            String fileName = "테스트_파일_이름";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            String fileFullName = "저장되는_파일_이름";
            String contentType = "text/plain";
            Long fileSize = 1024L;
            String fileUrl = "http://example.com/" + fileFullName;

            UploadFileMetaData uploadFileMetaData = mock(UploadFileMetaData.class);
            when(uploadFileMetaData.fileName()).thenReturn(fileFullName);
            when(uploadFileMetaData.contentType()).thenReturn(contentType);
            when(uploadFileMetaData.fileSize()).thenReturn(fileSize);
            when(uploadFileMetaData.fileUrl()).thenReturn(fileUrl);
            when(objectStorageClient.uploadMultipartFile(any(MultipartFile.class), any(String.class), any(StorageContext.class))).thenReturn(uploadFileMetaData);

            FileMetadata fileMetadata = mock(FileMetadata.class);
            when(fileMetadata.getId()).thenReturn(userId);
            when(fileMetadata.getFileName()).thenReturn(fileFullName);
            when(fileMetadata.getContentType()).thenReturn(contentType);
            when(fileMetadata.getFileSize()).thenReturn(fileSize);
            when(fileMetadata.getFileUrl()).thenReturn(fileUrl);
            when(fileMetadataService.save(userId, uploadFileMetaData.fileName(), uploadFileMetaData.contentType(), uploadFileMetaData.fileSize(),
                uploadFileMetaData.fileUrl())).thenReturn(fileMetadata);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetUploadAssetFile assetUploadAssetFile = assetFileService.uploadMultipartFile(file, userId, fileName);

            // then
            assertThat(assetUploadAssetFile.fileName()).isEqualTo(fileMetadata.getFileName());
            assertThat(assetUploadAssetFile.contentType()).isEqualTo(fileMetadata.getContentType());
            assertThat(assetUploadAssetFile.fileSize()).isEqualTo(fileMetadata.getFileSize());
            assertThat(assetUploadAssetFile.fileUrl()).isEqualTo(fileMetadata.getFileUrl());
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_SIZE를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유효성_검증에_실패하여_INVALID_SIZE를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            Long userId = 1L;
            String fileName = "테스트_파일_이름";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_SIZE);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, userId, fileName))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_SIZE);
                });
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_CONTENT_TYPE를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유효성_검증에_실패하여_INVALID_CONTENT_TYPE를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            Long userId = 1L;
            String fileName = "테스트_파일_이름";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_CONTENT_TYPE);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, userId, fileName))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_CONTENT_TYPE);
                });
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_EXTENSION를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유효성_검증에_실패하여_INVALID_EXTENSION를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            Long userId = 1L;
            String fileName = "테스트_파일_이름";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_EXTENSION);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, userId, fileName))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_EXTENSION);
                });
        }

        @Test
        @DisplayName("파일 업로드에 실패한 경우 예외를 발생시킨다")
        void 파일_업로드에_실패한_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            Long userId = 1L;
            String fileName = "테스트_파일_이름";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            doThrow(RuntimeException.class).when(objectStorageClient).uploadMultipartFile(any(MultipartFile.class), any(String.class), any(StorageContext.class));

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, userId, fileName))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Disabled(value = "UploadMultipartFiles는 Deprecated 되었습니다.")
    @Nested
    @DisplayName("MultipartFiles 업로드 테스트")
    class UploadMultipartFiles {

        @Test
        @DisplayName("모든 파일의 메타데이터에 대한 유효성 검증에 통과하고, 모든 파일 업로드를 비동기적으로 수행하여 성공하면 AssetUploadAssetFile 리스트를 반환한다")
        void 모든_파일의_메타데이터에_대한_유효성_검증에_통과하고_모든_파일_업로드에_성공하면_AssetUploadAssetFile_리스트를_반환한다() {
            // given
            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file1)).thenReturn(verifyResult);
            when(fileMetaValidator.validateMultipartFile(file2)).thenReturn(verifyResult);

            UploadFileMetaData uploadFileMetaData1 = mock(UploadFileMetaData.class);
            String fileFullName1 = "저장되는_파일_이름1";
            when(uploadFileMetaData1.fileName()).thenReturn(fileFullName1);
            when(uploadFileMetaData1.contentType()).thenReturn("text/plain");
            when(uploadFileMetaData1.fileSize()).thenReturn(1024L);
            when(uploadFileMetaData1.fileUrl()).thenReturn("http://example.com/" + fileFullName1);
            when(objectStorageClient.uploadMultipartFile(file1, UUID.randomUUID().toString(), StorageContext.FILE)).thenReturn(uploadFileMetaData1);

            UploadFileMetaData uploadFileMetaData2 = mock(UploadFileMetaData.class);
            String fileFullName2 = "저장되는_파일_이름2";
            when(uploadFileMetaData2.fileName()).thenReturn(fileFullName2);
            when(uploadFileMetaData2.contentType()).thenReturn("text/plain");
            when(uploadFileMetaData2.fileSize()).thenReturn(1024L);
            when(uploadFileMetaData2.fileUrl()).thenReturn("http://example.com/" + fileFullName2);
            when(objectStorageClient.uploadMultipartFile(file2, UUID.randomUUID().toString(), StorageContext.FILE)).thenReturn(uploadFileMetaData2);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            List<AssetUploadAssetFile> assetUploadAssetFiles = assetFileService.uploadMultipartFiles(List.of(file1, file2));

            // then
            assetUploadAssetFiles.forEach(assetUploadAssetFile -> {
                assertThat(assetUploadAssetFile.fileName()).isIn("test1.jpg", "test2.jpg");
                assertThat(assetUploadAssetFile.contentType()).isIn("image/jpeg", "image/png");
            });
        }

        @Test
        @DisplayName("파일 리스트 중 하나라도 메타데이터 유효성 검증에 실패하면 예외를 발생시킨다")
        void 파일_리스트_중_하나라도_메타데이터_유효성_검증에_실패하면_예외를_발생시킨다() {
            // given
            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);

            VerifyResult verifyResult1 = mock(VerifyResult.class);
            when(verifyResult1.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file1)).thenReturn(verifyResult1);

            VerifyResult verifyResult2 = mock(VerifyResult.class);
            when(verifyResult2.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_SIZE);
            when(fileMetaValidator.validateMultipartFile(file2)).thenReturn(verifyResult2);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFiles(List.of(file1, file2)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_SIZE);
                });
        }

        @Test
        @DisplayName("파일 리스트 중 두 개 이상이 메타데이터 유효성 검증에 실패하면 먼저 검증을 시도한 데이터에 대한 예외를 발생시킨다")
        void 파일_리스트_중_두_개_이상이_메타데이터_유효성_검증에_실패하면_먼저_검증을_시도한_데이터에_대한_예외를_발생시킨다() {
            // given
            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);

            VerifyResult verifyResult1 = mock(VerifyResult.class);
            when(verifyResult1.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_SIZE);
            when(fileMetaValidator.validateMultipartFile(file1)).thenReturn(verifyResult1);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFiles(List.of(file1, file2)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_SIZE);
                });
        }

        @Test
        @DisplayName("모든 파일에 대한 업로드를 비동기적으로 수행하는 도중 하나라도 실패하면 업로드 된 파일을 모두 삭제하고 예외를 발생시킨다")
        void 모든_파일에_대한_업로드를_비동기적으로_수행하는_도중_하나라도_실패하면_업로드_된_파일을_모두_삭제하고_예외를_발생시킨다() {
            // given
            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);
            MultipartFile file3 = mock(MultipartFile.class);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file1)).thenReturn(verifyResult);
            when(fileMetaValidator.validateMultipartFile(file2)).thenReturn(verifyResult);
            when(fileMetaValidator.validateMultipartFile(file3)).thenReturn(verifyResult);

            UploadFileMetaData uploadFileMetaData1 = mock(UploadFileMetaData.class);
            String fileFullName1 = "저장되는_파일_이름1";
            when(uploadFileMetaData1.fileName()).thenReturn(fileFullName1);
            when(uploadFileMetaData1.contentType()).thenReturn("text/plain");
            when(uploadFileMetaData1.fileSize()).thenReturn(1024L);
            when(uploadFileMetaData1.fileUrl()).thenReturn("http://example.com/" + fileFullName1);
            when(objectStorageClient.uploadMultipartFile(file1, UUID.randomUUID().toString(), StorageContext.FILE)).thenReturn(uploadFileMetaData1);

            RuntimeException testException = new RuntimeException("test exception");
            doThrow(testException).when(objectStorageClient).uploadMultipartFile(file2, "저장되는_파일_이름_2", StorageContext.FILE);

            UploadFileMetaData uploadFileMetaData3 = mock(UploadFileMetaData.class);
            String fileFullName3 = "저장되는_파일_이름1";
            when(uploadFileMetaData3.fileName()).thenReturn(fileFullName3);
            when(uploadFileMetaData3.contentType()).thenReturn("text/plain");
            when(uploadFileMetaData3.fileSize()).thenReturn(1024L);
            when(uploadFileMetaData3.fileUrl()).thenReturn("http://example.com/" + fileFullName1);
            when(objectStorageClient.uploadMultipartFile(file3, fileFullName3, StorageContext.FILE)).thenReturn(uploadFileMetaData3);

            when(objectStorageClient.deleteFile(uploadFileMetaData1.fileName(), StorageContext.FILE)).thenReturn(true);
            when(objectStorageClient.deleteFile(uploadFileMetaData3.fileName(), StorageContext.FILE)).thenReturn(true);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFiles(List.of(file1, file2, file3)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    verify(objectStorageClient, times(2)).deleteFile(any(String.class), any(StorageContext.class));
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.FILE_UPLOAD_FAILED);
                    assertThat((String) customException.getDetail()).isNotBlank();
                });
        }


        @Test
        @DisplayName("모든 파일에 대한 업로드를 비동기적으로 수행하는 도중 두 개 이상이 실패하면 모든 예외에 대한 정보가 포함된 예외를 발생시킨다")
        void 모든_파일에_대한_업로드를_비동기적으로_수행하는_도중_두_개_이상이_실패하면_모든_예외에_대한_정보가_포함된_예외를_발생시킨다() {
            // given
            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);
            MultipartFile file3 = mock(MultipartFile.class);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file1)).thenReturn(verifyResult);
            when(fileMetaValidator.validateMultipartFile(file2)).thenReturn(verifyResult);
            when(fileMetaValidator.validateMultipartFile(file3)).thenReturn(verifyResult);

            UploadFileMetaData uploadFileMetaData1 = mock(UploadFileMetaData.class);
            String fileFullName1 = "저장되는_파일_이름1";
            when(uploadFileMetaData1.fileName()).thenReturn(fileFullName1);
            when(uploadFileMetaData1.contentType()).thenReturn("text/plain");
            when(uploadFileMetaData1.fileSize()).thenReturn(1024L);
            when(uploadFileMetaData1.fileUrl()).thenReturn("http://example.com/" + fileFullName1);
            when(objectStorageClient.uploadMultipartFile(file1, fileFullName1, StorageContext.FILE)).thenReturn(uploadFileMetaData1);

            RuntimeException testException1 = new RuntimeException("test exception1");
            doThrow(testException1).when(objectStorageClient).uploadMultipartFile(file2, "저장되는_파일_이름_2", StorageContext.FILE);

            RuntimeException testException2 = new RuntimeException("test exception2");
            doThrow(testException2).when(objectStorageClient).uploadMultipartFile(file3, "저장되는_파일_이름_3", StorageContext.FILE);

            when(objectStorageClient.deleteFile(uploadFileMetaData1.fileName(), StorageContext.FILE)).thenReturn(true);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFiles(List.of(file1, file2, file3)))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    verify(objectStorageClient, times(1)).deleteFile(any(String.class), any(StorageContext.class));
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.FILE_UPLOAD_FAILED);
                    assertThat((String) customException.getDetail()).isEqualTo("File upload interrupted: java.lang.RuntimeException: test exception1, java.lang.RuntimeException: test exception2");
                });
        }
    }

    @Disabled(value = "UploadStreaming는 Deprecated 되었습니다.")
    @Nested
    @DisplayName("스트리밍 방식 파일 업로드 테스트")
    class UploadStreaming {
        @Test
        @DisplayName("Request 객체로부터 필요한 정보를 가져온 뒤 유효성 검증에 성공하고 파일 업로드를 스트리밍 방식으로 수행하여 정상적으로 AssetUploadAssetFile을 반환한다")
        void Request_객체로부터_필요한_정보를_가져온_뒤_유효성_검증에_성공하고_파일_업로드를_스트리밍_방식으로_수행하여_정상적으로_AssetUploadAssetFile을_반환한다() throws IOException {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);

            ServletInputStream inputStream = mock(ServletInputStream.class);
            when(request.getInputStream()).thenReturn(inputStream);
            when(request.getContentType()).thenReturn("application/octet-stream");

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateStreamFile(request.getContentType()))
                .thenReturn(verifyResult);

            UploadFileResult uploadFileResult = mock(UploadFileResult.class);
            when(uploadFileResult.fileName()).thenReturn("streamed_test");
            when(uploadFileResult.contentType()).thenReturn("application/octet-stream");
            when(objectStorageClient.uploadStreaming(inputStream, StorageContext.FILE, request.getContentType()))
                .thenReturn(uploadFileResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetUploadAssetFile assetUploadAssetFile = assetFileService.uploadStreaming(request);

            // then
            assertThat(assetUploadAssetFile.fileName()).isEqualTo("streamed_test");
            assertThat(assetUploadAssetFile.contentType()).isEqualTo("application/octet-stream");
        }

        @Test
        @DisplayName("Request 객체로부터 InputStream을 가져오는 도중 IOException이 발생하면 예외를 발생시킨다")
        void Request_객체로부터_InputStream을_가져오는_도중_IOException이_발생하면_예외를_발생시킨다() throws IOException {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);

            doThrow(IOException.class).when(request).getInputStream();

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadStreaming(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(CommonErrorType.OCCURRED_IO_EXCEPTION);
                });
        }

        @Test
        @DisplayName("스트림 파일의 메타데이터 유효성 검증에 실패하여 INVALID_CONTENT_TYPE를 반환받은 경우 예외를 발생시킨다")
        void 스트림_파일의_메타데이터_유효성_검증에_실패하여_INVALID_CONTENT_TYPE를_반환받은_경우_예외를_발생시킨다() throws IOException {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);

            ServletInputStream inputStream = mock(ServletInputStream.class);
            when(request.getInputStream()).thenReturn(inputStream);
            when(request.getContentType()).thenReturn("image/jpeg");

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_CONTENT_TYPE);
            when(fileMetaValidator.validateStreamFile(request.getContentType())).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadStreaming(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_CONTENT_TYPE);
                });
        }

        @Test
        @DisplayName("UploadStreaming 수행 도중 예외가 발생하면 예외를 그대로 던진다")
        void UploadStreaming_수행_도중_예외가_발생하면_예외를_그대로_던진다() throws IOException {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);

            ServletInputStream inputStream = mock(ServletInputStream.class);
            when(request.getInputStream()).thenReturn(inputStream);
            when(request.getContentType()).thenReturn("application/octet-stream");

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateStreamFile(request.getContentType()))
                .thenReturn(verifyResult);

            RuntimeException testException = new RuntimeException("test exception");
            when(objectStorageClient.uploadStreaming(inputStream, StorageContext.FILE, request.getContentType())).thenThrow(testException);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadStreaming(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.FILE_UPLOAD_FAILED);
                    assertThat((String) customException.getDetail()).isNotBlank();
                });
        }
    }

    @Nested
    @DisplayName("ID를 통한 파일 메타데이터 조회 테스트")
    class GetFileMetadata {

        @Test
        @DisplayName("파일 메타데이터 ID를 통해 엔티티 조회에 성공하고 접근 권한 검증에 성공할 경우 파일 메타데이터 정보를 반환한다")
        void 파일_메타데이터_ID를_통해_엔티티_조회에_성공하고_접근_권한_검증에_성공할_경우_파일_메타데이터_정보를_반환한다() {
            // given
            Long userId = 1L;
            Long fileMetadataId = 2L;

            FileMetadata fileMetadata = mock(FileMetadata.class);
            when(fileMetadata.getId()).thenReturn(fileMetadataId);
            when(fileMetadata.getUserId()).thenReturn(userId);
            when(fileMetadata.getFileName()).thenReturn("테스트용이미지");
            when(fileMetadata.getContentType()).thenReturn("image/jpeg");
            when(fileMetadata.getFileSize()).thenReturn(1024L);
            when(fileMetadata.getFileUrl()).thenReturn("http://example.com/test.jpg");
            when(fileMetadata.isDeleted()).thenReturn(false);

            when(fileMetadataService.findById(fileMetadataId)).thenReturn(fileMetadata);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetGetFileMetadataResponseView result = assetFileService.getFileMetadata(userId, fileMetadataId);

            // then
            assertThat(result.fileMetadataId()).isEqualTo(fileMetadataId);
            assertThat(result.fileName()).isEqualTo("테스트용이미지");
            assertThat(result.contentType()).isEqualTo("image/jpeg");
            assertThat(result.fileSize()).isEqualTo(1024L);
            assertThat(result.fileUrl()).isEqualTo("http://example.com/test.jpg");
        }

        @Test
        @DisplayName("파일 메타데이터 ID를 통해 엔티티를 찾지 못했을 경우 예외를 그대로 던진다")
        void 파일_메타데이터_ID를_통해_엔티티를_찾지_못했을_경우_예외를_그대로_던진다() {
            // given
            Long userId = 1L;
            Long fileMetadataId = 2L;

            CustomException customException = mock(CustomException.class);
            doThrow(customException).when(fileMetadataService).findById(fileMetadataId);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.getFileMetadata(userId, fileMetadataId))
                .isEqualTo(customException);
        }

        @Test
        @DisplayName("파일 메타데이터 ID를 통해 엔티티 조회에 성공하고 접근 권한 검증에 실패할 경우 예외를 발생시킨다")
        void 파일_메타데이터_ID를_통해_엔티티_조회에_성공하고_접근_권한_검증에_실패할_경우_예외를_발생시킨다() {
            // given
            Long userId = 1L;
            Long fileMetadataId = 2L;

            FileMetadata fileMetadata = mock(FileMetadata.class);
            when(fileMetadata.getUserId()).thenReturn(3L);

            when(fileMetadataService.findById(fileMetadataId)).thenReturn(fileMetadata);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.getFileMetadata(userId, fileMetadataId))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.UNAUTHORIZED_FILE_ACCESS);
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
        }

        @Test
        @DisplayName("조회한 파일 메타데이터가 삭제되어 있는 상태라면 예외를 발생시킨다")
        void 조회한_파일_메타데이터가_삭제되어_있는_상태라면_예외를_발생시킨다() {
            // given
            Long userId = 1L;
            Long fileMetadataId = 2L;

            FileMetadata fileMetadata = mock(FileMetadata.class);
            when(fileMetadata.getUserId()).thenReturn(userId);
            when(fileMetadata.isDeleted()).thenReturn(true);

            when(fileMetadataService.findById(fileMetadataId)).thenReturn(fileMetadata);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.getFileMetadata(userId, fileMetadataId))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.NOT_ALLOW_ACCESS_DELETED_FILE);
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
        }
    }

    @Nested
    @DisplayName("USER ID를 통해 파일 메타데이터 조회 테스트")
    class FindFileMetadata {

        @Test
        @DisplayName("사용자 ID를 통해 모든 파일 메타데이터의 조회에 성공했다면 삭제된 파일을 필터링한 뒤 파일 메타데이터 정보 리스트를 반환한다")
        void 사용자_ID를_통해_모든_파일_메타데이터의_조회에_성공했다면_삭제된_파일을_필터링한_뒤_파일_메타데이터_정보_리스트를_반환한다() {
            // given
            Long userId = 1L;

            FileMetadata fileMetadata1 = createMockFileMetadata(1L, userId, "파일1", "image/jpeg", 2048L, "http://example.com/file1.jpg", false);
            FileMetadata fileMetadata2 = createMockFileMetadata(2L, userId, "파일2", "image/png", 4096L, "http://example.com/file2.png", false);
            FileMetadata deletedFileMetadata = mock(FileMetadata.class);
            when(deletedFileMetadata.isDeleted()).thenReturn(true);

            when(fileMetadataService.findAllByUserId(userId)).thenReturn(List.of(fileMetadata1, fileMetadata2, deletedFileMetadata));

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetFindFileMetadataResponseView fileMetadata = assetFileService.findFileMetadata(userId);

            // then
            assertThat(fileMetadata.fileMetadataList()).hasSize(2);
            assertThat(fileMetadata.fileMetadataList()).extracting("fileMetadataId")
                .containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("사용자 ID를 통해 파일 메타데이터의 조회 결과가 1개도 존재하지 않다면 빈 리스트를 반환한다")
        void 사용자_ID를_통해_파일_메타데이터의_조회_결과가_1개도_존재하지_않다면_빈_리스트를_반환한다() {
            // given
            Long userId = 1L;

            when(fileMetadataService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetFindFileMetadataResponseView fileMetadata = assetFileService.findFileMetadata(userId);

            // then
            assertThat(fileMetadata.fileMetadataList()).isEmpty();
        }

        @Test
        @DisplayName("사용자 ID를 통해 파일 메타데이터의 조회 결과가 모두 삭제된 파일이라면 빈 리스트를 반환한다")
        void 사용자_ID를_통해_파일_메타데이터의_조회_결과가_모두_삭제된_파일이라면_빈_리스트를_반환한다() {
            // given
            Long userId = 1L;

            FileMetadata fileMetadata1 = mock(FileMetadata.class);
            when(fileMetadata1.isDeleted()).thenReturn(true);
            FileMetadata fileMetadata2 = mock(FileMetadata.class);
            when(fileMetadata2.isDeleted()).thenReturn(true);

            when(fileMetadataService.findAllByUserId(userId)).thenReturn(List.of(fileMetadata1, fileMetadata2));

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(fileMetadataService, objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetFindFileMetadataResponseView fileMetadata = assetFileService.findFileMetadata(userId);

            // then
            assertThat(fileMetadata.fileMetadataList()).isEmpty();
        }

        private FileMetadata createMockFileMetadata(Long fileMetadataId, Long userId, String fileName, String contentType, long fileSize, String fileUrl,
            boolean isDeleted) {
            FileMetadata fileMetadata = mock(FileMetadata.class);
            when(fileMetadata.getId()).thenReturn(fileMetadataId);
            when(fileMetadata.getFileName()).thenReturn(fileName);
            when(fileMetadata.getContentType()).thenReturn(contentType);
            when(fileMetadata.getFileSize()).thenReturn(fileSize);
            when(fileMetadata.getFileUrl()).thenReturn(fileUrl);
            when(fileMetadata.isDeleted()).thenReturn(isDeleted);
            return fileMetadata;
        }
    }

}