package com.eventty.eventtynextgen.asset.file.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileMetadata Entity 단위 테스트")
class FileMetadataTest {

    @Nested
    @DisplayName("Getter 테스트")
    class Getter {

        @Test
        @DisplayName("파일 메타데이터의 fileName에 '/'가 포함되어 있지 않는 경우 그대로 반환한다")
        void 파일_메타데이터의_fileName에_슬래시가_포함되어_있지_않는_경우_그대로_반환한다() {
            // given
            String fileName = "테스트파일이름0";

            FileMetadata fileMetadata = FileMetadata.of(1L, fileName, "image/png", "http://example.com/file");

            // when
            String fileNameFromEntity = fileMetadata.getFileName();

            // then
            assertThat(fileNameFromEntity).isEqualTo("테스트파일이름0");
        }

        @Test
        @DisplayName("파일 메타데이터의 fileName에 '/'가 하나 포함되어 있는 경우  '/' 이후의 문자열을 반환한다")
        void 파일_메타데이터의_fileName에_슬래시가_하나_포함되어_있는_경우_슬래시_이후의_문자열을_반환한다() {
            // given
            String fileName = "1/테스트파일이름0";

            FileMetadata fileMetadata = FileMetadata.of(1L, fileName, "image/png", "http://example.com/file");

            // when
            String fileNameFromEntity = fileMetadata.getFileName();

            // then
            assertThat(fileNameFromEntity).isEqualTo("테스트파일이름0");
        }

        @Test
        @DisplayName("파일 메타데이터의 fileName에 '/'가 두 개 이상 포함되어 있는 경우 처음 '/' 이후의 문자열을 반환한다")
        void 파일_메타데이터의_fileName에_슬래시가_두_개_이상_포함되어_있는_경우_처음_슬래시_이후의_문자열을_반환한다() {
            // given
            String fileName = "1/테스트파일이름/0";

            FileMetadata fileMetadata = FileMetadata.of(1L, fileName, "image/png", "http://example.com/file");

            // when
            String fileNameFromEntity = fileMetadata.getFileName();

            // then
            assertThat(fileNameFromEntity).isEqualTo("테스트파일이름/0");
        }
    }
}