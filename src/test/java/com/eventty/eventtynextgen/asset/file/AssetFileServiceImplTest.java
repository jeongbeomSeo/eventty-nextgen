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
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyFileMetaResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssetFileServiceImpl 단위 테스트")
class AssetFileServiceImplTest {

    @Mock
    private ObjectStorageClient objectStorageClient;

    @Mock
    private FileMetaValidator fileMetaValidator;

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

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            UploadFileResult uploadFileResult = mock(UploadFileResult.class);
            when(uploadFileResult.fileName()).thenReturn("test.jpg");
            when(uploadFileResult.contentType()).thenReturn("image/jpeg");
            when(objectStorageClient.uploadMultipartFile(file, StorageContext.FILE)).thenReturn(uploadFileResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when
            AssetUploadAssetFile assetUploadAssetFile = assetFileService.uploadMultipartFile(file);

            // then
            assertThat(assetUploadAssetFile.fileName()).isEqualTo("test.jpg");
            assertThat(assetUploadAssetFile.contentType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_SIZE를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유효성_검증에_실패하여_INVALID_SIZE를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_SIZE);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file))
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

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_CONTENT_TYPE);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file))
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

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_EXTENSION);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file))
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

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateMultipartFile(file)).thenReturn(verifyResult);

            doThrow(RuntimeException.class).when(objectStorageClient).uploadMultipartFile(file, StorageContext.FILE);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file))
                .isInstanceOf(RuntimeException.class);
        }
    }

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

            UploadFileResult uploadFileResult1 = mock(UploadFileResult.class);
            when(uploadFileResult1.fileName()).thenReturn("test1.jpg");
            when(uploadFileResult1.contentType()).thenReturn("image/jpeg");
            when(uploadFileResult1.isSuccess()).thenReturn(true);
            when(objectStorageClient.uploadMultipartFile(file1, StorageContext.FILE)).thenReturn(uploadFileResult1);

            UploadFileResult uploadFileResult2 = mock(UploadFileResult.class);
            when(uploadFileResult2.fileName()).thenReturn("test2.jpg");
            when(uploadFileResult2.contentType()).thenReturn("image/png");
            when(uploadFileResult2.isSuccess()).thenReturn(true);
            when(objectStorageClient.uploadMultipartFile(file2, StorageContext.FILE)).thenReturn(uploadFileResult2);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            UploadFileResult uploadFileResult1 = mock(UploadFileResult.class);
            when(uploadFileResult1.fileName()).thenReturn("test1.jpg");
            when(uploadFileResult1.isSuccess()).thenReturn(true);
            when(objectStorageClient.uploadMultipartFile(file1, StorageContext.FILE)).thenReturn(uploadFileResult1);

            RuntimeException testException = new RuntimeException("test exception");
            doThrow(testException).when(objectStorageClient).uploadMultipartFile(file2, StorageContext.FILE);

            UploadFileResult uploadFileResult3 = mock(UploadFileResult.class);
            when(uploadFileResult3.fileName()).thenReturn("test3.jpg");
            when(uploadFileResult3.isSuccess()).thenReturn(true);

            when(objectStorageClient.uploadMultipartFile(file3, StorageContext.FILE)).thenReturn(uploadFileResult3);

            when(objectStorageClient.deleteFile(uploadFileResult1.fileName(), StorageContext.FILE)).thenReturn(true);
            when(objectStorageClient.deleteFile(uploadFileResult3.fileName(), StorageContext.FILE)).thenReturn(true);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            UploadFileResult uploadFileResult1 = mock(UploadFileResult.class);
            when(uploadFileResult1.fileName()).thenReturn("test1.jpg");
            when(uploadFileResult1.isSuccess()).thenReturn(true);
            when(objectStorageClient.uploadMultipartFile(file1, StorageContext.FILE)).thenReturn(uploadFileResult1);

            RuntimeException testException1 = new RuntimeException("test exception1");
            doThrow(testException1).when(objectStorageClient).uploadMultipartFile(file2, StorageContext.FILE);

            RuntimeException testException2 = new RuntimeException("test exception2");
            doThrow(testException2).when(objectStorageClient).uploadMultipartFile(file3, StorageContext.FILE);

            when(objectStorageClient.deleteFile(uploadFileResult1.fileName(), StorageContext.FILE)).thenReturn(true);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, gcsIoExecutor);

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

}