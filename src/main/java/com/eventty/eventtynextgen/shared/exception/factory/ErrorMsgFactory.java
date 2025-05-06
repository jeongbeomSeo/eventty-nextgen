package com.eventty.eventtynextgen.shared.exception.factory;

import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ErrorMsgFactory {

    public static Map<String, String> createFieldErrorMsg(String field, String msg) {
        return Map.of(
            "field", field,
            "message", msg
        );
    }

}
