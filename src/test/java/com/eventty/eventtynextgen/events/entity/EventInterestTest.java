package com.eventty.eventtynextgen.events.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.events.entity.EventInterest.EventInterestStatus;
import com.eventty.eventtynextgen.events.fixture.EventInterestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EventInterest 클래스 단위 테스트")
class EventInterestTest {

    @Nested
    @DisplayName("관심 행사 삭제 상태 변경 테스트")
    class UpdateDeletedStatus {

        @Test
        @DisplayName("관심 행사 정보 상태 변경 인자로 ACTIVE가 들어올 경우 사용자 삭제 정보가 갱신된다")
        void 관심_행사_상태_변경_인자로_ACTIVE가_들어올_경우_사용자_삭제_정보가_갱신된다() {
            // given
            EventInterest eventInterest = EventInterestFixture.createEventInterest();

            // when
            eventInterest.updateDeletedStatus(EventInterestStatus.ACTIVE);

            // then
            assertThat(eventInterest.isDeleted()).isFalse();
            assertThat(eventInterest.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("관심 행사 정보 상태 변경 인자로 DELETED가 들어올 경우 사용자 삭제 정보가 갱신된다")
        void 관심_행사_상태_변경_인자로_DELETED가_들어올_경우_사용자_삭제_정보가_갱신된다() {
            // given
            EventInterest eventInterest = EventInterestFixture.createEventInterest();

            // when
            eventInterest.updateDeletedStatus(EventInterestStatus.DELETED);

            // then
            assertThat(eventInterest.isDeleted()).isTrue();
            assertThat(eventInterest.getDeletedAt()).isNotNull();
        }
    }

}