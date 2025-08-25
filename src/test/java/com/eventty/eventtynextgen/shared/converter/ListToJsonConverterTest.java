package com.eventty.eventtynextgen.shared.converter;

import static org.assertj.core.api.Assertions.assertThat;

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
    }
}