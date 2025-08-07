package com.eventty.eventtynextgen.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.user.entity.User.UserStatus;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User 클래스 단위 테스트")
class UserTest {

    @Nested
    @DisplayName("사용자 개인 정보 업데이트 테스트")
    class UpdatePersonalInfo {

        @Test
        @DisplayName("사용자 개인 정보를 성공적으로 업데이트한다")
        void 사용자_개인_정보를_성공적으로_업데이트한다() {
            // given
            User user = UserFixture.createUser();
            String newNickname = "newNickname";
            String newPhoneNumber = "newPhoneNumber";
            String newBirth = "1999.01.01";

            // when
            user.updatePersonalInfo(newNickname, newPhoneNumber, newBirth);

            // then
            assertThat(user.getName()).isEqualTo(newNickname);
            assertThat(user.getPhone()).isEqualTo(newPhoneNumber);
            assertThat(user.getBirth()).isEqualTo(newBirth);
        }
    }

    @Nested
    @DisplayName("사용자 삭제 상태 변경 테스트")
    class updateDeleteStatus {
        @Test
        @DisplayName("사용자 상태 변경 인자로 ACTIVE가 들어올 경우 사용자 삭제 정보가 사라진다")
        void 사용자_상태_변경_인자로_ACTIVE가_들어올_경우_사용자_삭제_정보가_사라진다() {
            // given
            User user = UserFixture.createUser();

            // when
            user.updateDeleteStatus(UserStatus.ACTIVE);

            // then
            assertThat(user.isDeleted()).isFalse();
            assertThat(user.getDeleteTime()).isNull();
        }

        @Test
        @DisplayName("사용자 상태 변경 인자로 DELETED가 들어올 경우 사용자 삭제 정보가 갱신된다")
        void 사용자_상태_변경_인자로_DELETED가_들어올_경우_사용자_삭제_정보가_갱신된다() {
            // given
            User user = UserFixture.createUser();

            // when
            user.updateDeleteStatus(UserStatus.DELETED);

            // then
            assertThat(user.isDeleted()).isTrue();
            assertThat(user.getDeleteTime()).isNotNull();
        }
    }
}