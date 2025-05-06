package com.eventty.eventtynextgen.shared.exception.factory.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "error-type")
public class ErrorTypeProperties {

    private Map<String, Map<String, String>> messages = new HashMap<>();

    public String getMessage(HttpStatus status, String errorCode) {
        return Optional.ofNullable(messages.get(String.valueOf(status.value())))
            .map(m -> m.get(errorCode))
            .orElse("[UNKNOWN ERROR]");
    }
}
