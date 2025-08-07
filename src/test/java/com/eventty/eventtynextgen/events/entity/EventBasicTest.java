package com.eventty.eventtynextgen.events.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.events.entity.EventBasic.EventStatus;
import com.eventty.eventtynextgen.events.fixture.EventBasicFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EventBasic 클래스 단위 테스트")
class EventBasicTest {

    @Nested
    @DisplayName("기본 행사 정보 삭제 상태 변경 테스트")
    class UpdateDeletedStatus {

        @Test
        @DisplayName("기본 행사 정보 상태 변경 인자로 ACTIVE가 들어올 경우 사용자 삭제 정보가 사라진다")
        void 기본_행사_정보_상태_변경_인자로_ACTIVE가_들어올_경우_사용자_삭제_정보가_사라진다() {
            // given
            EventBasic eventBasic = EventBasicFixture.createEventBasic();

            // when
            eventBasic.updateDeletedStatus(EventStatus.ACTIVE);

            // then
            assertThat(eventBasic.isDeleted()).isFalse();
            assertThat(eventBasic.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("기본 행사 정보 상태 변경 인자로 DELETED가 들어올 경우 사용자 삭제 정보가 사라진다")
        void 기본_행사_정보_상태_변경_인자로_DELETEDE가_들어올_경우_사용자_삭제_정보가_사라진다() {
            // given
            EventBasic eventBasic = EventBasicFixture.createEventBasic();

            // when
            eventBasic.updateDeletedStatus(EventStatus.DELETED);

            // then
            assertThat(eventBasic.isDeleted()).isTrue();
            assertThat(eventBasic.getDeletedAt()).isNotNull();
        }
    }

}