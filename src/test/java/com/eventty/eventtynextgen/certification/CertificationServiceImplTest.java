package com.eventty.eventtynextgen.certification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.certification.certificationcode.CertificationCodeServiceImpl;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationServiceImpl 단위 테스트")
public class CertificationServiceImplTest {

    @DisplayName("비즈니스 로직 - 로그인")
    @Nested
    class Login {

        @Test
        @DisplayName("이메일과 비밀번호로 활성화 상태인 유저를 찾을 경우 로그인에 성공하고 생성한 토큰을 반환한다.")
        void 이메일과_비밀번호로_활성화_상태인_유저를_찾을_경우_로그인에_성공하고_생성한_토큰을_반환한다() {
            // given
            String loginId = "example@gmail.com";
            String password = "password1234";

            User user = mock(User.class);
            when(user.getId()).thenReturn(1L);
            when(user.getEmail()).thenReturn(loginId);
            when(user.getName()).thenReturn("홍길동");

            CertificationService certificationService = new CertificationServiceImpl();

            // when
            CertificationLoginResponseView result = certificationService.login(loginId, password);

            // then
            assertThat(result.userId()).isNotNull();
            assertThat(result.email()).isEqualTo(loginId);
            assertThat(result.name()).isNotBlank();
            assertThat(result.tokenInfo().tokenType()).isNotBlank();
            assertThat(result.tokenInfo().accessToken()).isNotBlank();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않을 경우 로그인에 실패하고 예외를 발생시킨다.")
        void 비밀번호가_일치하지_않을_경우_로그인에_실패하고_예외를_발생시킨다() {

        }

        @Test
        @DisplayName("이메일과 비밀번호로 삭제 상태인 유저를 찾을 경우 로그인에 실패하고 예외를 발생시킨다.")
        void 이메일과_비밀번호로_삭제_상태인_유저를_찾을_경우_로그인에_실패하고_예외를_발생시킨다() {

        }

        @Test
        @DisplayName("이메일과 비밀번호로 사용자를 찾지 못했을 경우 로그인에 실패하고 예외를 발생시킨다.")
        void 이메일과_비밀번호로_사용자를_찾지_못했을_경우_로그인에_실패하고_예외를_발생시킨다() {

        }

    }

}
