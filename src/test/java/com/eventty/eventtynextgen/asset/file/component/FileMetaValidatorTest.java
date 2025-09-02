package com.eventty.eventtynextgen.asset.file.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyFileMetaResult;
import com.eventty.eventtynextgen.asset.file.component.FileMetaValidator.VerifyResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileMetaValidator 단위 테스트")
class FileMetaValidatorTest {

    @Nested
    @DisplayName("파일 메타데이터 검증 테스트")
    class ValidateFileTest {

        @Test
        @DisplayName("모든 유효성 검증에 성공적으로 통과하는 경우 VALID를 반환한다")
        void 모든_유효성_검증에_성공적으로_통과하는_경우_VALID를_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            long size = 1024 * 1024; // 1MB
            when(file.getSize()).thenReturn(size);
            when(file.getContentType()).thenReturn("application/json");
            when(file.getOriginalFilename()).thenReturn("test.json");

            // when
            VerifyResult verifyResult = new FileMetaValidator().validateMultipartFile(file);

            // then
            assertThat(verifyResult.getVerifyFileMetaResult()).isEqualTo(VerifyFileMetaResult.VALID);
        }

        @Test
        @DisplayName("파일의 크기가 제한은 넘어선 경우 INVALID_SIZE를 반환한다")
        void 파일의_크기가_제한을_넘어선_경우_INVALID_SIZE를_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            long size = 25 * 1024 * 1024 + 1; // 25MB + 1Byte
            when(file.getSize()).thenReturn(size);

            // when
            VerifyResult verifyResult = new FileMetaValidator().validateMultipartFile(file);

            // then
            assertThat(verifyResult.getVerifyFileMetaResult()).isEqualTo(VerifyFileMetaResult.INVALID_SIZE);
        }

        @Test
        @DisplayName("파일의 컨텐츠 타입이 허용되지 않는 경우 INVALID_CONTENT_TYPE를 반환한다")
        void 파일의_컨텐츠_타입이_허용되지_않는_경우_INVALID_CONTENT_TYPE를_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            long size = 1024 * 1024; // 1MB
            when(file.getSize()).thenReturn(size);
            when(file.getContentType()).thenReturn("image/jpeg");

            // when
            VerifyResult verifyResult = new FileMetaValidator().validateMultipartFile(file);

            // then
            assertThat(verifyResult.getVerifyFileMetaResult()).isEqualTo(VerifyFileMetaResult.INVALID_CONTENT_TYPE);
        }

        @Test
        @DisplayName("파일의 확장자가 허용되지 않는 경우 INVALID_EXTENSION를 반환한다")
        void 파일의_확장자가_허용되지_않는_경우_INVALID_EXTENSION를_반환한다() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            long size = 1024 * 1024; // 1MB
            when(file.getSize()).thenReturn(size);
            when(file.getContentType()).thenReturn("application/json");
            when(file.getOriginalFilename()).thenReturn("test.jpg");

            // when
            VerifyResult verifyResult = new FileMetaValidator().validateMultipartFile(file);

            // then
            assertThat(verifyResult.getVerifyFileMetaResult()).isEqualTo(VerifyFileMetaResult.INVALID_EXTENSION);
        }
    }
}