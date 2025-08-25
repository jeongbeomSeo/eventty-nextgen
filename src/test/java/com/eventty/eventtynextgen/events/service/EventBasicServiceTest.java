package com.eventty.eventtynextgen.events.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.events.component.CreateEventValidator;
import com.eventty.eventtynextgen.events.component.CreateEventValidator.VerifyEventBasicResult;
import com.eventty.eventtynextgen.events.component.CreateEventValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.repository.EventBasicRepository;
import com.eventty.eventtynextgen.events.service.EventBasicService.EventBasicArgs;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
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
    private CreateEventValidator createEventValidator;


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
            when(verifyResult.getDetails()).thenReturn("");

            EventBasic savedEventBasic = mock(EventBasic.class);

            when(createEventValidator.validateEventBasic(args.eventStartAt(), args.eventEndAt(), args.imageUrls(), args.participantLimitPolicy(),
                args.maxParticipants())).thenReturn(verifyResult);
            when(eventBasicRepository.save(any(EventBasic.class))).thenReturn(savedEventBasic);

            EventBasicService eventBasicService = new EventBasicService(eventBasicRepository, createEventValidator);

            // when
            EventBasic result = eventBasicService.saveEventBasic(args);

            // then
            assertThat(result).isEqualTo(savedEventBasic);
        }

        @Test
        @DisplayName("유효성 검증에 실패하여 이벤트 주최 날짜 유효성 검증 실패 결과를 받은 경우 예외를 발생시킨다")
        void 유효성_검증에_실패하여_이벤트_주최_날짜_유효성_검증_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given

            // when

            // then
        }

        @Test
        @DisplayName("유효성 검증에 실패하여 이벤트 이미지 유효성 검증 실패 결과를 받은 경우 예외를 발생시칸다")
        void 유효성_검증에_실패하여_이벤트_이미지_유효성_검증_실패_결과를_받은_경우_예외를_발생시킨다() {
            // given

            // when

            // then
        }

        @Test
        @DisplayName("DB에_저장시_제약조건이_위배되었을_경우_저장에_실패한다")
        void DB에_저장시_제약조건이_위배되었을_경우_저장에_실패한다() {
            // given

            // when

            // then
        }
    }
}