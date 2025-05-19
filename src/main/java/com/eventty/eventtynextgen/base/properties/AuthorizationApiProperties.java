package com.eventty.eventtynextgen.base.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "authorization")
public class AuthorizationApiProperties {

    private Map<String, String> swagger = new HashMap<>();
    private Map<String, String> certification = new HashMap<>();
    private Map<String, String> user = new HashMap<>();

    public String getPermission(String apiName) {
        String apiKey = apiName.toLowerCase().replace("_", "-");
        if (swagger.containsKey(apiKey)) {
            return swagger.get(apiKey);
        } else if (certification.containsKey(apiKey)) {
            return certification.get(apiKey);
        } else if (user.containsKey(apiKey)) {
            return user.get(apiKey);
        }

        return null;
    }

}
