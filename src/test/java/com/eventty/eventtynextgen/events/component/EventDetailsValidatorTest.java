package com.eventty.eventtynextgen.events.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventDetailsValidator 단위 테스트")
class EventDetailsValidatorTest {

    @Nested
    @DisplayName("Event Details Info 유효성 검사 메서드 테스트")
    class ValidateEventDetails {

        @Test
        @DisplayName("모든 유효성 검증에 통과한다면 VERIFIED 결과를 반환한다.")
        void 모든_유효성_검증에_통과한다면_VERIFIED_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.plusDays(1);

            EventDetailsValidator eventDetailsValidator = new EventDetailsValidator();

            // when
            EventDetailsValidator.VerifyResult verifyResult = eventDetailsValidator.validateEventDetails(eventStartAt, eventEndAt);

            // then
            assertThat(verifyResult.getVerifyEventDetailsResult()).isEqualTo(EventDetailsValidator.VerifyEventDetailsResult.VERIFIED);
        }

        @Test
        @DisplayName("예약 날짜 유효성 검증에 실패했다면 ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START 결과를 반환한다.")
        void 예약_날짜_유효성_검증에_실패했다면_ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START_결과를_반환한다() {
            // given
            LocalDateTime eventStartAt = LocalDateTime.now();
            LocalDateTime eventEndAt = eventStartAt.minusDays(1);

            EventDetailsValidator eventDetailsValidator = new EventDetailsValidator();

            // when
            EventDetailsValidator.VerifyResult verifyResult = eventDetailsValidator.validateEventDetails(eventStartAt, eventEndAt);

            // then
            assertThat(verifyResult.getVerifyEventDetailsResult()).isEqualTo(
                EventDetailsValidator.VerifyEventDetailsResult.ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START);
        }
    }

}