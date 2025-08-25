package com.eventty.eventtynextgen.events.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.events.component.EventDetailsValidator;
import com.eventty.eventtynextgen.events.component.EventDetailsValidator.VerifyEventDetailsResult;
import com.eventty.eventtynextgen.events.component.EventDetailsValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventDetails;
import com.eventty.eventtynextgen.events.repository.EventDetailsRepository;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventDetailsService 단위 테스트")
class EventDetailsServiceTest {

    @Mock
    private EventDetailsValidator eventDetailsValidator;

    @Mock
    private EventDetailsRepository eventDetailsRepository;

    @Nested
    @DisplayName("EventDetails 저장 메서드 단위 테스트")
    class SaveEventDetailsTest {

        @Test
        @DisplayName("유효성 검증에 통과하고 엔티티 생성을 성공적으로 수행하여 저장에 성공한다")
        void 유효성_검증에_통과하고_엔티티_생성을_성공적으로_수행하여_저장에_성공한다() {
            // given
            Long eventBasicId = 1L;
            String description = "";
            LocalDateTime applyStartAt = LocalDateTime.now().plusDays(1);
            LocalDateTime applyEndAt = applyStartAt.plusDays(1);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventDetailsResult()).thenReturn(EventDetailsValidator.VerifyEventDetailsResult.VERIFIED);

            EventDetails savedEventDetails = mock(EventDetails.class);

            when(eventDetailsValidator.validateEventDetails(applyStartAt, applyEndAt)).thenReturn(verifyResult);
            when(eventDetailsRepository.save(any(EventDetails.class))).thenReturn(savedEventDetails);

            EventDetailsService eventDetailsService = new EventDetailsService(eventDetailsValidator, eventDetailsRepository);

            // when
            EventDetails result = eventDetailsService.saveEventDetails(eventBasicId, description, applyStartAt, applyEndAt);

            // then
            assertThat(result).isEqualTo(savedEventDetails);
        }

        @Test
        @DisplayName("예약 신청 날짜 유효성 검증에 실패 결과를 받은 경우 예외를 발생시킨다")
        void 예약_신청_날짜_유효성_검증에_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given
            Long eventBasicId = 1L;
            String description = "";
            LocalDateTime applyStartAt = LocalDateTime.now().plusDays(1);
            LocalDateTime applyEndAt = applyStartAt.minusDays(1);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventDetailsResult()).thenReturn(VerifyEventDetailsResult.ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START);
            when(verifyResult.getDetails()).thenReturn("details");

            when(eventDetailsValidator.validateEventDetails(applyStartAt, applyEndAt)).thenReturn(verifyResult);

            EventDetailsService eventDetailsService = new EventDetailsService(eventDetailsValidator, eventDetailsRepository);

            // when & then
            assertThatThrownBy(() -> eventDetailsService.saveEventDetails(eventBasicId, description, applyStartAt, applyEndAt))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(EventsErrorType.ILLEGAL_APPLY_END_BEFORE_START);
                    assertThat(customException.getDetail()).isEqualTo(verifyResult.getDetails());
                });
        }

        @Test
        @DisplayName("DB에 저장시 제약조건이 위배되었을 경우 저장에 실패한다")
        void DB에_저장시_제약조건이_위배되었을_경우_저장에_실패한다() {
            // given
            Long eventBasicId = null;
            String description = "";
            LocalDateTime applyStartAt = LocalDateTime.now().plusDays(1);
            LocalDateTime applyEndAt = applyStartAt.plusDays(1);

            VerifyResult verifyResult = mock(VerifyResult.class);
            when(verifyResult.getVerifyEventDetailsResult()).thenReturn(EventDetailsValidator.VerifyEventDetailsResult.VERIFIED);

            when(eventDetailsValidator.validateEventDetails(applyStartAt, applyEndAt)).thenReturn(verifyResult);
            doThrow(ConstraintViolationException.class).when(eventDetailsRepository).save(any(EventDetails.class));

            EventDetailsService eventDetailsService = new EventDetailsService(eventDetailsValidator, eventDetailsRepository);

            // when & then
            assertThatThrownBy(() -> eventDetailsService.saveEventDetails(eventBasicId, description, applyStartAt, applyEndAt))
                .isInstanceOf(ConstraintViolationException.class);
        }
    }
}