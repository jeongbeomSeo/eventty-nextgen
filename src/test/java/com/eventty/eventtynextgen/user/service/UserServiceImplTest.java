package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.fixture.UserFixture;
import com.eventty.eventtynextgen.auth.fixture.UserSignupRequestFixture;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.UserErrorType;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.repository.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service 단위 테스트")
class UserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @DisplayName("비즈니스 로직 - 회원가입")
    @Nested
    class Signup {

        @Test
        @DisplayName("user signup - 새로운 회원은 회원가입에 `성공`한다.")
        void 새로운_회원은_회원가입에_성공한다() {
            // given
            UserSignupRequest request = UserSignupRequestFixture.successUserSignupRequest();
            User user = UserFixture.createUserByUserSignupRequest(request);

            when(userRepository.existsByAuthUserId(request.getAuthUserId())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserService userService = new UserServiceImpl(userRepository);

            // when
            Long result = userService.signup(request);

            // then
            assertThat(result).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("user signup - 이미 auth id가 등록되어 있는 회원 가입 요청은 `실패`한다.")
        void 이미_가입되어_있는_회원은_회원가입에_실패한다() {
            // given
            UserSignupRequest request = UserSignupRequestFixture.successUserSignupRequest();

            UserServiceImpl userService = new UserServiceImpl(userRepository);

            when(userRepository.existsByAuthUserId(request.getAuthUserId())).thenReturn(true);

            // when & then
            try {
                userService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(UserErrorType.AUTH_USER_ID_ALREADY_EXISTS);
            }
        }

        @DisplayName("회원가입 입력값 유효성 검증 테스트")
        @Nested
        class UserSignupRequestValidationTest {

            @Test
            @DisplayName("request validation - auth user id가 null일 경우 이와 관련된 `예외`가 발생한다.")
            void 회원가입_입력값_AUTH_USER_ID_검증으로_인해_객체_생성에_실패한다() {
                // given

                // when & then
                assertThatThrownBy(UserSignupRequestFixture::invalidAuthUserIdRequest)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("authUserID 값은 필수값 입니다.");
            }

            @Test
            @DisplayName("request validation - 이름이 null일 경우 이와 관련된 `예외`가 발생한다.")
            void 회원가입_입력값_이름_검증으로_인해_객체_생성에_실패한다() {
                // given

                // when & then
                assertThatThrownBy(UserSignupRequestFixture::invalidNameRequest)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 필수값 입니다.");
            }

            @Test
            @DisplayName("input validation - 핸드폰 번호 포맷이 유효하지 않은 경우 이와 관련된 `예외`가 발생한다.")
            void 회원가입_입력값_핸드폰_번호_검증으로_인해_객체_생성에_실패한다() {
                // given

                // when & then
                assertThatThrownBy(UserSignupRequestFixture::invalidPhoneNumberRequest)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("핸드폰 번호 형식이 올바르지 않습니다. 형식: 000-0000-0000");
            }

            @Test
            @DisplayName("input validation - 생년월일 포맷이 유효하지 않은 경우 이와 관련된 `예외`가 발생한다.")
            void 회원가입_입력값_생년월일_검증으로_인해_객체_생성에_실패한다() {
                // given

                // when & then
                assertThatThrownBy(UserSignupRequestFixture::invalidBirthdateFormatRequest)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("생년 월일 형식이 올바르지 않습니다. 형식: `YYYY.MM.DD` 또는 `YYYY-MM-DD`");
            }
        }
    }


}