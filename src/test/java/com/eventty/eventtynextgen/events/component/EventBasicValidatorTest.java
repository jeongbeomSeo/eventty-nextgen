package com.eventty.eventtynextgen.events.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.asset.core.ObjectStorageClient;
import com.eventty.eventtynextgen.asset.core.ObjectStorageClient.Context;
import com.eventty.eventtynextgen.events.component.EventBasicValidator.VerifyEventBasicResult;
import com.eventty.eventtynextgen.events.component.EventBasicValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventBasicValidator 단위 테스트")
class EventBasicValidatorTest {

    @Mock
    private ObjectStorageClient storageService;

    @Nested
    @DisplayName("Event Basic Info 유효성 검증 메서드 테스트")
    class ValidateEventBasicInfoTest {

        @Test
        @DisplayName("참여 인원에 제한이 없는 상황에서 모든 유효성 검증에 통과한다면 VERIFIED 결과를 반환한다.")
        void 참여_인원에_제한이_없는_상황에서_모든_유효성_검증에_통과한다면_VERIFIED_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.UNLIMITED;
            Integer eventParticipantLimit = null;

            when(storageService.existsFile(imageUrls.get(0), Context.EVENT_IMAGE)).thenReturn(true);
            when(storageService.existsFile(imageUrls.get(1), Context.EVENT_IMAGE)).thenReturn(true);

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.VERIFIED);
        }

        @Test
        @DisplayName("참여 인원에 제한이 있는 상황에서 모든 유효성 검증에 통과한다면 VERIFIED 결과를 반환한다.")
        void 참여_인원에_제한이_있는_상황에서_모든_유효성_검증에_통과한다면_VERIFIED_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.LIMITED;
            Integer eventParticipantLimit = 100;

            when(storageService.existsFile(imageUrls.get(0), Context.EVENT_IMAGE)).thenReturn(true);
            when(storageService.existsFile(imageUrls.get(1), Context.EVENT_IMAGE)).thenReturn(true);

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

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
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.UNLIMITED;
            Integer eventParticipantLimit = null;

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

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
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.UNLIMITED;
            Integer eventParticipantLimit = null;

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

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
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.UNLIMITED;
            Integer eventParticipantLimit = null;

            when(storageService.existsFile(imageUrls.get(0), Context.EVENT_IMAGE)).thenReturn(true);
            when(storageService.existsFile(imageUrls.get(1), Context.EVENT_IMAGE)).thenReturn(false);
            when(storageService.existsFile(imageUrls.get(2), Context.EVENT_IMAGE)).thenReturn(false);

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.ILLEGAL_EVENT_IMAGE);
            assertThat(verifyResult.getDetails()).isNotBlank();
        }

        @Test
        @DisplayName("참여 인원 정책이 제한되어 있는 경우에 참여 인원 수가 음수인 경우 ILLEGAL_ARGUMENT_MAX_PARTICIPANTS 결과를 반환한다.")
        void 참여_인원_정책이_제한되어_있는_경우에_참여_인원_수가_음수인_경우_ILLEGAL_ARGUMENT_MAX_PARTICIPANTS_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.LIMITED;
            Integer eventParticipantLimit = -100;

            when(storageService.existsFile(imageUrls.get(0), Context.EVENT_IMAGE)).thenReturn(true);
            when(storageService.existsFile(imageUrls.get(1), Context.EVENT_IMAGE)).thenReturn(true);

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.ILLEGAL_ARGUMENT_MAX_PARTICIPANTS);
        }

        @Test
        @DisplayName("참여 인원 정책이 제한되어 있는 경우 침야 인원 수가 0인 경우 ILLEGAL_ARGUMENT_MAX_PARTICIPANTS 결과를 반환한다.")
        void 참여_인원_정책이_제한되어_있는_경우에_참여_인원_수가_0인_경우_ILLEGAL_ARGUMENT_MAX_PARTICIPANTS_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);
            List<String> imageUrls = List.of("https://example.com/image1.png", "https://example.com/image2.png");
            EventParticipantLimitPolicyType eventParticipantLimitPolicyType = EventParticipantLimitPolicyType.LIMITED;
            Integer eventParticipantLimit = 0;

            when(storageService.existsFile(imageUrls.get(0), Context.EVENT_IMAGE)).thenReturn(true);
            when(storageService.existsFile(imageUrls.get(1), Context.EVENT_IMAGE)).thenReturn(true);

            EventBasicValidator eventBasicValidator = new EventBasicValidator(storageService);

            // when
            VerifyResult verifyResult = eventBasicValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls, eventParticipantLimitPolicyType, eventParticipantLimit);

            // then
            assertThat(verifyResult.getVerifyEventBasicResult()).isEqualTo(VerifyEventBasicResult.ILLEGAL_ARGUMENT_MAX_PARTICIPANTS);
        }
    }
}