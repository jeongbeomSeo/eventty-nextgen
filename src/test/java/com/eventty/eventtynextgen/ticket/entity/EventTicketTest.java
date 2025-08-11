package com.eventty.eventtynextgen.ticket.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.ticket.entity.EventTicket.EventTicketStatus;
import com.eventty.eventtynextgen.ticket.fixture.EventTicketFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EventTicket 클래스 단위 테스트")
class EventTicketTest {

    @Nested
    @DisplayName("행사 티켓 삭제 상태 변경 테스트")
    class UpdateDeletedStatus {

        @Test
        @DisplayName("행사 티켓 상태 변경 인자로 ACTIVE가 들어올 경우 사용자 삭제 정보가 갱신된다")
        void 행사_티켓_상태_변경_인자로_ACTIVE가_들어올_경우_사용자_삭제_정보가_갱신된다() {
            // given
            EventTicket eventTicket = EventTicketFixture.createEventTicket();

            // when
            eventTicket.updateDeletedStatus(EventTicketStatus.ACTIVE);

            // then
            assertThat(eventTicket.isDeleted()).isFalse();
            assertThat(eventTicket.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("행사 티켓 상태 변경 인자로 DELETED가 들어올 경우 사용자 삭제 정보가 갱신된다")
        void 행사_티켓_상태_변경_인자로_DELETED가_들어올_경우_사용자_삭제_정보가_갱신된다() {
            // given
            EventTicket eventTicket = EventTicketFixture.createEventTicket();

            // when
            eventTicket.updateDeletedStatus(EventTicketStatus.DELETED);

            // then
            assertThat(eventTicket.isDeleted()).isTrue();
            assertThat(eventTicket.getDeletedAt()).isNotNull();
        }
    }
}