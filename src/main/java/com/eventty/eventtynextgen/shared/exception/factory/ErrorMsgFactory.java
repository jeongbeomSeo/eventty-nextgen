package com.eventty.eventtynextgen.shared.exception.factory;

import java.util.Map;

public final class ErrorMsgFactory {

    private ErrorMsgFactory() {}

    public static Map<String, String> createFieldErrorMsg(String field, String msg) {
        return Map.of(
            "field", field,
            "message", msg
        );
    }

}
