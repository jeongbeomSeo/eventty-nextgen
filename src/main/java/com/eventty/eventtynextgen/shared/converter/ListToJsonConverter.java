package com.eventty.eventtynextgen.shared.converter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ListToJsonConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        try {
            return OBJECT_MAPPER.writeValueAsString(strings);
        } catch (JsonProcessingException e) {
            log.error("ListToJsonConverter: Error while converting list to json \nmessage: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        try {
            if (string == null || string.isEmpty()) {
                return Collections.emptyList();
            }
            return OBJECT_MAPPER.readValue(string, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("ListToJsonConverter: Error while converting json to list \nmessage: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
