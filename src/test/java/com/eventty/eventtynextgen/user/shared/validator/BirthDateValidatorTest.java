package com.eventty.eventtynextgen.user.shared.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BirthDateValidatorTest {

    @Nested
    @DisplayName("생년월일 포맷 검증 테스트")
    class isValid {

        @Test
        @DisplayName("yyyy.mm.dd 형식일 경우 검증에 성공한다")
        void dot_형식으로_구성된_올바른_형태인_생년월일은_검증에_성공한다() {
            // given
            String birthDate = "1990.01.01";
            BirthDateValidator validator = new BirthDateValidator();

            // when
            boolean valid = validator.isValid(birthDate, null);

            // then
            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("yyyy-mm-dd 형식일 경우 검증에 성공한다")
        void dash_형식으로_구성된_올바른_형태인_생년월일은_검증에_성공한다() {
            // given
            String birthDate = "1990-01-01";
            BirthDateValidator validator = new BirthDateValidator();

            // when
            boolean valid = validator.isValid(birthDate, null);

            // then
            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("dot 형식과 dash 형식이 섞여 있는 형태인 생년월일은 검증에 실패한다")
        void dot_형식과_dash_형식이_섞여_있는_형태인_생년월일은_검증에_실패한다() {
            // given
            String birthDate = "1990.01-01";
            BirthDateValidator validator = new BirthDateValidator();

            // when
            boolean valid = validator.isValid(birthDate, null);

            // then
            assertThat(valid).isFalse();
        }

        @Test
        @DisplayName("생년월일 인자에 null 값이 들어온 경우 검증에 실패하고 false를 반환한다")
        void 생년월일_인자에_null_값이_들어온_경우_검증에_실패하고_false를_반환한다() {
            // given
            String birthDate = null;
            BirthDateValidator validator = new BirthDateValidator();

            // when
            boolean valid = validator.isValid(birthDate, null);

            // then
            assertThat(valid).isFalse();
        }
    }
}