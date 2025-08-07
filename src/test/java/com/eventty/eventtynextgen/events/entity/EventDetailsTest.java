package com.eventty.eventtynextgen.events.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.events.entity.EventDetails.EventDetailsStatus;
import com.eventty.eventtynextgen.events.fixture.EventDetailsFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EventDetails 클래스 단위 테스트")
class EventDetailsTest {

    @Nested
    @DisplayName("행사 세부 정보 삭제 상태 변경 테스트")
    class UpdateDeletedStatus {

        @Test
        @DisplayName("행사 세부 정보 상태 변경 인자로 ACTIVE가 들어올 경우 사용자 삭제 정보가 사라진다")
        void 행사_세부_정보_상태_변경_인자로_ACTIVE가_들어올_경우_사용자_삭제_정보가_사라진다() {
            // given
            EventDetails eventDetails = EventDetailsFixture.createEventDetails();

            // when
            eventDetails.updateDeletedStatus(EventDetailsStatus.ACTIVE);

            // then
            assertThat(eventDetails.isDeleted()).isFalse();
            assertThat(eventDetails.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("행사 세부 정보 상태 변경 인자로 DELETED가 들어올 경우 사용자 삭제 정보가 사라진다")
        void 행사_세부_정보_상태_변경_인자로_DELETED가_들어올_경우_사용자_삭제_정보가_사라진다() {
            // given
            EventDetails eventDetails = EventDetailsFixture.createEventDetails();

            // when
            eventDetails.updateDeletedStatus(EventDetailsStatus.DELETED);

            // then
            assertThat(eventDetails.isDeleted()).isTrue();
            assertThat(eventDetails.getDeletedAt()).isNotNull();
        }
    }
}