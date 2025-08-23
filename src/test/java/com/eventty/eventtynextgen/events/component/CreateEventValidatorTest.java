package com.eventty.eventtynextgen.events.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.component.StorageService;
import com.eventty.eventtynextgen.events.component.CreateEventValidator.VerifyEventBasicResult;
import com.eventty.eventtynextgen.events.component.CreateEventValidator.VerifyResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEventValidator 단위 테스트")
class CreateEventValidatorTest {

    @Mock
    private StorageService storageService;

    @Nested
    @DisplayName("Event Basic Info 유효성 검증 메서드 테스트")
    class ValidateEventBasicInfoTest {

        @Test
        @DisplayName("모든 유효성 검증에 통과한다면 VERIFIED 결과를 반환한다.")
        void 모든_유효성_검증에_통과한다면_VERIFIED_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");

            when(storageService.fileExists(imageUrls.get(0))).thenReturn(true);
            when(storageService.fileExists(imageUrls.get(1))).thenReturn(true);

            CreateEventValidator createEventValidator = new CreateEventValidator(storageService);

            // when
            VerifyResult verifyResult = createEventValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.VERIFIED);
        }

        @Test
        @DisplayName("eventEndAt이 eventStartAt 이전이라면 ILLEGAL_EVENT_END_BEFORE_START 결과를 반환한다.")
        void eventEndAt이_eventStartAt_이전이라면_ILLEGAL_EVENT_END_BEFORE_START_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.minusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");

            CreateEventValidator createEventValidator = new CreateEventValidator(storageService);

            // when
            VerifyResult verifyResult = createEventValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.ILLEGAL_EVENT_END_BEFORE_START);
            assertThat(verifyResult.getDetails()).isNotBlank();
        }

        @Test
        @DisplayName("imageUrls가 null이라면 유효성 검증에 통과한다.")
        void imageUrls가_null이라면_유효성_검증에_통과한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = null;

            CreateEventValidator createEventValidator = new CreateEventValidator(storageService);

            // when
            VerifyResult verifyResult = createEventValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.VERIFIED);
        }

        @Test
        @DisplayName("imageUrls에 스토리지에 존재하지 않는 URL이 포함되어 있다면 ILLEGAL_EVENT_IMAGE 결과를 반환한다.")
        void imageUrls에_스토리지에_존재하지_않는_URL이_포함되어_있다면_ILLEGAL_EVENT_IMAGE_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png", "https://example.com/image3.png");

            when(storageService.fileExists(imageUrls.get(0))).thenReturn(true);
            when(storageService.fileExists(imageUrls.get(1))).thenReturn(false);
            when(storageService.fileExists(imageUrls.get(2))).thenReturn(false);

            CreateEventValidator createEventValidator = new CreateEventValidator(storageService);

            // when
            VerifyResult verifyResult = createEventValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.ILLEGAL_EVENT_IMAGE);
            assertThat(verifyResult.getDetails()).isNotBlank();
        }
    }
}