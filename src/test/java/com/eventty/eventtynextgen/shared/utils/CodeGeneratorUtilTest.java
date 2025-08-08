package com.eventty.eventtynextgen.shared.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

@DisplayName("Code Generator Utility 클래스 단위 테스트")
class CodeGeneratorUtilTest {

    @Nested
    @DisplayName("무작위 문자열 생성 테스트")
    class GenerateRandomCode {
        @RepeatedTest(100)
        @DisplayName("4부터 20까지 랜덤하게 주어지는 길이 인자를 받아 여러번 랜덤 코드를 생성하는데 성공한다")
        void 랜덤하게_주어지는_길이_인자를_받아_여러번_랜덤_코드_생성하는데_성공한다() {
            // given
            int len = RandomUtils.nextInt(4, 21);

            // when
            String randomCode = CodeGeneratorUtils.generateRandomCode(len);

            // then
            assertThat(randomCode).isNotBlank();
            assertThat(randomCode.length()).isEqualTo(len);
        }
    }
}