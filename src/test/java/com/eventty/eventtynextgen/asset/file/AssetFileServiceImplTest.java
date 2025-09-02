package com.eventty.eventtynextgen.asset.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.Context;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.UploadFileResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyFileMetaResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import com.eventty.eventtynextgen.asset.file.response.AssetUploadAssetFile;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AssetErrorType;
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

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Nested
    @DisplayName("MultipartFile 업로드 테스트")
    class UploadMultipartFile {

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 성공하고, Context를 가져와 파일 업로드를 수행하여 정상적으로 AssetUploadAssetFile을 반환한다")
        void 메타_데이터의_유효성_검증에_성공하고_Context를_가져와_파일_업로드를_수행하여_정상적으로_AssetUploadAssetFile을_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            String context = "file";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            UploadFileResult uploadFileResult = mock(UploadFileResult.class);
            when(uploadFileResult.fileName()).thenReturn("test.jpg");
            when(uploadFileResult.contentType()).thenReturn("image/jpeg");
            when(uploadFileResult.contentLength()).thenReturn(1024L);
            when(objectStorageClient.uploadMultipartFile(file, Context.FILE)).thenReturn(uploadFileResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when
            AssetUploadAssetFile assetUploadAssetFile = assetFileService.uploadMultipartFile(file, context);

            // then
            assertThat(assetUploadAssetFile.fileName()).isEqualTo("test.jpg");
            assertThat(assetUploadAssetFile.contentType()).isEqualTo("image/jpeg");
            assertThat(assetUploadAssetFile.contentLength()).isEqualTo(1024L);
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_SIZE를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유효성_검증에_실패하여_INVALID_SIZE를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            String context = "file";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_SIZE);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, context))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_SIZE);
                });
        }

        @Test
        @DisplayName("메타 데이터의 유효성 검증에 실패하여 INVALID_CONTENT_TYPE를 반환받은 경우 예외를 발생시킨다")
        void 메타_데이터의_유혀성_검증에_실패하여_INVALID_CONTENT_TYPE를_반환받은_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            String context = "file";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_CONTENT_TYPE);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, context))
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
            String context = "file";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.INVALID_EXTENSION);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, context))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.INVALID_FILE_EXTENSION);
                });
        }

        @Test
        @DisplayName("File Context를 가져오지 못한 경우 예외를 발생시킨다")
        void File_Context를_가져오지_못한_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            String context = "image";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, context))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(AssetErrorType.ILLEGAL_ARGUMENT_FILE_CONTEXT);
                });
        }

        @Test
        @DisplayName("파일 업로드에 실패한 경우 예외를 발생시킨다")
        void 파일_업로드에_실패한_경우_예외를_발생시킨다() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            String context = "file";

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyFileMetaResult()).thenReturn(VerifyFileMetaResult.VALID);
            when(fileMetaValidator.validateFile(file)).thenReturn(verifyResult);

            doThrow(RuntimeException.class).when(objectStorageClient).uploadMultipartFile(file, Context.FILE);

            AssetFileServiceImpl assetFileService = new AssetFileServiceImpl(objectStorageClient, fileMetaValidator, threadPoolTaskExecutor);

            // when & then
            assertThatThrownBy(() -> assetFileService.uploadMultipartFile(file, context))
                .isInstanceOf(RuntimeException.class);
        }
    }

}