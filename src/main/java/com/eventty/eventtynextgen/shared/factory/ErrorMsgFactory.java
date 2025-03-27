package com.eventty.eventtynextgen.shared.factory;

import java.util.Map;

public final class ErrorMsgFactory {

    private ErrorMsgFactory() {}

    public static Map<String, String> createFieldErrorMsg(String field, String msg) {
        return Map.ofEntries(
            Map.entry("field", field),
            Map.entry("message", msg)
        );
    }

}
