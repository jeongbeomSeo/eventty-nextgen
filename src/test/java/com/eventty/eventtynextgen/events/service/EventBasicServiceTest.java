package com.eventty.eventtynextgen.events.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.events.component.EventBasicValidator;
import com.eventty.eventtynextgen.events.component.EventBasicValidator.VerifyEventBasicResult;
import com.eventty.eventtynextgen.events.component.EventBasicValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.repository.EventBasicRepository;
import com.eventty.eventtynextgen.events.service.EventBasicService.EventBasicArgs;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Event Basic Service 단위 테스트")
class EventBasicServiceTest {

    @Mock
    private EventBasicRepository eventBasicRepository;

    @Mock
    private EventBasicValidator eventBasicValidator;


    @Nested
    @DisplayName("Event Basic 저장 메서드 테스트")
    class SaveEventBasic {

        @Test
        @DisplayName("유효성 검증에 통과하고 엔티티 생성을 성공적으로 수행하여 저장에 성공한다")
        void 유효성_검증에_통과하고_엔티티_생성을_성공적으로_수행하여_저장에_성공한다() {
            // given
            EventBasicArgs args = EventBasicArgs.builder()
                .hostId(1L)
                .title("test title")
                .imageUrls(List.of("https://example.com/image1.png", "https://example.com/image2.png"))
                .category(EventCategoryType.ETC)
                .eventStartAt(LocalDateTime.now())
                .eventEndAt(LocalDateTime.now().plusDays(1))
                .participantLimitPolicy(EventParticipantLimitPolicyType.LIMITED)
                .maxParticipants(10)
                .location("test location")
                .build();

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventBasicResult()).thenReturn(VerifyEventBasicResult.VERIFIED);

            EventBasic savedEventBasic = mock(EventBasic.class);

            when(eventBasicValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants())).thenReturn(verifyResult);
            when(eventBasicRepository.save(any(EventBasic.class))).thenReturn(savedEventBasic);

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, eventBasicValidator);

            // when
            EventBasic result = eventBasicService.saveEventBasic(args);

            // then
            assertThat(result).isEqualTo(savedEventBasic);
        }

        @Test
        @DisplayName("이벤트 주최 날짜 유효성 검증에 실패 결과를 받은 경우 예외를 발생시킨다")
        void 이벤트_주최_날짜_유효성_검증에_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given
            EventBasicArgs args = EventBasicArgs.builder()
                .hostId(1L)
                .title("test title")
                .imageUrls(List.of("https://example.com/image1.png", "https://example.com/image2.png"))
                .category(EventCategoryType.ETC)
                .eventStartAt(LocalDateTime.now())
                .eventEndAt(LocalDateTime.now().minusDays(1))
                .participantLimitPolicy(EventParticipantLimitPolicyType.LIMITED)
                .maxParticipants(10)
                .location("test location")
                .build();

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventBasicResult()).thenReturn(VerifyEventBasicResult.ILLEGAL_EVENT_END_BEFORE_START);
            when(verifyResult.getDetails()).thenReturn("details");

            when(eventBasicValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants()))
                .thenReturn(verifyResult);

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, eventBasicValidator);

            // when & then
            assertThatThrownBy(() -> eventBasicService.saveEventBasic(args))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(EventsErrorType.ILLEGAL_EVENT_END_BEFORE_START);
                    assertThat(customException.getDetail()).isEqualTo(verifyResult.getDetails());
                });
        }

        @Test
        @DisplayName("이벤트 이미지 유효성 검증에 실패 결과를 받은 경우 예외를 발생시칸다")
        void 이벤트_이미지_유효성_검증에_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given
            EventBasicArgs args = EventBasicArgs.builder()
                .hostId(1L)
                .title("test title")
                .imageUrls(List.of("fs://example.com/image1.png", "https://example.com/image2.png"))
                .category(EventCategoryType.ETC)
                .eventStartAt(LocalDateTime.now())
                .eventEndAt(LocalDateTime.now().plusDays(1))
                .participantLimitPolicy(EventParticipantLimitPolicyType.LIMITED)
                .maxParticipants(10)
                .location("test location")
                .build();

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventBasicResult()).thenReturn(VerifyEventBasicResult.ILLEGAL_EVENT_IMAGE);
            when(verifyResult.getDetails()).thenReturn("details");

            when(eventBasicValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants()))
                .thenReturn(verifyResult);

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, eventBasicValidator);

            // when & then
            assertThatThrownBy(() -> eventBasicService.saveEventBasic(args))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(EventsErrorType.ILLEGAL_EVENT_IMAGE);
                    assertThat(customException.getDetail()).isEqualTo(verifyResult.getDetails());
                });
        }

        @Test
        @DisplayName("참가자 제한 정책 유효성 검증에 실패 결과를 받은 경우 예외를 발생시킨다")
        void 참가자_제한_정책_유효성_검증에_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given
            EventBasicArgs args = EventBasicArgs.builder()
                .hostId(1L)
                .title("test title")
                .imageUrls(List.of("https://example.com/image1.png", "https://example.com/image2.png"))
                .category(EventCategoryType.ETC)
                .eventStartAt(LocalDateTime.now())
                .eventEndAt(LocalDateTime.now().plusDays(1))
                .participantLimitPolicy(EventParticipantLimitPolicyType.LIMITED)
                .maxParticipants(-100)
                .location("test location")
                .build();

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventBasicResult()).thenReturn(VerifyEventBasicResult.ILLEGAL_ARGUMENT_MAX_PARTICIPANTS);
            when(verifyResult.getDetails()).thenReturn("details");

            when(eventBasicValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants()))
                .thenReturn(verifyResult);

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, eventBasicValidator);

            // when & then
            assertThatThrownBy(() -> eventBasicService.saveEventBasic(args))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(EventsErrorType.ILLEGAL_MAX_PARTICIPANTS);
                    assertThat(customException.getDetail()).isEqualTo(verifyResult.getDetails());
                });
        }

        @Test
        @DisplayName("DB에 저장시 제약조건이 위배되었을 경우 저장에 실패한다")
        void DB에_저장시_제약조건이_위배되었을_경우_저장에_실패한다() {
            // given
            EventBasicArgs args = EventBasicArgs.builder()
                .hostId(null)
                .title("test title")
                .imageUrls(List.of("https://example.com/image1.png", "https://example.com/image2.png"))
                .category(EventCategoryType.ETC)
                .eventStartAt(LocalDateTime.now())
                .eventEndAt(LocalDateTime.now().plusDays(1))
                .participantLimitPolicy(EventParticipantLimitPolicyType.LIMITED)
                .maxParticipants(10)
                .location("test location")
                .build();

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventBasicResult()).thenReturn(VerifyEventBasicResult.VERIFIED);

            when(eventBasicValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants())).thenReturn(verifyResult);
            doThrow(ConstraintViolationException.class).when(eventBasicRepository).save(any(EventBasic.class));

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, eventBasicValidator);

            // when & then
            assertThatThrownBy(() -> eventBasicService.saveEventBasic(args))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }
}