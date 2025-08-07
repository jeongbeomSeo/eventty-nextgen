package com.eventty.eventtynextgen.user.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Password Encoder 단위 테스트")
class PasswordEncoderTest {

    @Nested
    @DisplayName("패스워드 인코딩 테스트")
    class Encode {
        @Test
        @DisplayName("비밀번호를 해싱한다.")
        void 비밀번호를_해싱한다() {
            // given
            CharSequence plainPassword = "<PASSWORD>";

            // when
            String encodedPassword = PasswordEncoder.encode(plainPassword);

            // then
            assertThat(encodedPassword).isNotBlank();
            assertThat(encodedPassword).isNotEqualTo(plainPassword.toString());
        }
    }

    @Nested
    @DisplayName("평문 패스워드와 해싱된 패스워드 비교 테스트")
    class Matches {
        @Test
        @DisplayName("인자로 들어온 평문 비밀번호와 해싱된 비밀번호를 비교한 결과가 성공일 경우 true를 반환한다")
        void 인자로_들어온_평문_비밀번호와_해싱된_비밀번호를_비교한_결과가_성공일_경우_true를_반환한다() {
            // given
            CharSequence plainPassword = "<PASSWORD>";
            String encodedPassword = PasswordEncoder.encode(plainPassword);

            // when
            boolean isMatched = PasswordEncoder.matches(plainPassword, encodedPassword);

            // then
            assertThat(isMatched).isTrue();
        }

        @Test
        @DisplayName("인자로 들어온 평문 비밀번호와 해싱된 비밀번호를 비교한 결과가 실패일 경우 false를 반환한다")
        void 인자로_들어온_평문_비밀번호와_해싱된_비밀번호를_비교한_결과가_실패일_경우_false를_반환한다() {
            // given
            CharSequence plainPassword = "<PASSWORD>";
            String encodedPassword = PasswordEncoder.encode(plainPassword);

            // when
            boolean isMatched = PasswordEncoder.matches(plainPassword + "1", encodedPassword);

            // then
            assertThat(isMatched).isFalse();
        }
    }
}