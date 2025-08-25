package com.eventty.eventtynextgen.shared.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("List to JSON Converter 단위 테스트")
class ListToJsonConverterTest {

    @Nested
    @DisplayName("List to json 변환 메서드 테스트")
    class ConvertToDatabaseColumn {

        @Test
        @DisplayName("String 제너릭 타입의 List 형식의 데이터를 JSON 형식의 문자열로 변환한다.")
        void String_제너릭_타입의_List_형식의_데이터를_JSON_형식의_문자열로_변환한다() throws JsonProcessingException {
            // given
            List<String> list = List.of("a", "b", "c");
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when
            String json = listToJsonConverter.convertToDatabaseColumn(list);

            // then
            assertThat(json).isEqualTo((new ObjectMapper().writeValueAsString(list)));
        }

        @Test
        @DisplayName("List에 직렬화 불가능한 객체가 포함된 경우 예외가 발생한다")
        void List에_직렬화_불가능한_객체가_포함된_경우_에외가_발생한다() {
            // given
            List<Object> list = List.of(new Object());
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when & then
            assertThatThrownBy(() -> listToJsonConverter.convertToDatabaseColumn((List<String>)(List<?>)list))
                .isInstanceOf(RuntimeException.class);
        }

    }

    @Nested
    @DisplayName("Json to List 변환 메서드 테스트")
    class convertToEntityAttribute {

        @Test
        @DisplayName("JSON 형식의 문자열을 String 제너릭 타입의 List 형식의 데이터로 변환한다.")
        void JSON_형식의_문자열을_String_제너릭_타입의_List_형식의_데이터로_변환한다() throws JsonProcessingException {
            List<String> list = List.of("a", "b", "c");
            String json = new ObjectMapper().writeValueAsString(list);
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when
            List<String> result = listToJsonConverter.convertToEntityAttribute(json);

            // then
            assertThat(result).isEqualTo(list);
        }

        @Test
        @DisplayName("null이 들어오면 빈 리스트를 반환한다")
        void null이_들어오면_빈_리스트를_반환한다() {
            // given
            String json = null;
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when
            List<String> result = listToJsonConverter.convertToEntityAttribute(json);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열이 들어오면 빈 리스트를 반환한다")
        void 빈_문자열이_들어오면_빈_리스트를_반환한다() {
            // given
            String json = "";
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when
            List<String> result = listToJsonConverter.convertToEntityAttribute(json);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("잘못된 JSON 문자열이 들어오면 예외가 발생한다.")
        void 잘못된_JSON_문자열이_들어오면_예외가_발생한다() {
            // given
            String invalidJson = "{invalid json}";
            ListToJsonConverter listToJsonConverter = new ListToJsonConverter();

            // when & then
            assertThatThrownBy(() -> listToJsonConverter.convertToEntityAttribute(invalidJson))
                .isInstanceOf(RuntimeException.class);
        }
    }


}