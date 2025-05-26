package com.eventty.eventtynextgen.base.properties;

import com.eventty.eventtynextgen.base.enums.ApiName;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "authorization")
public class AuthorizationApiProperties {

    private Map<String, Map<String, String>> permissions;

    public String getPermission(String appName, ApiName apiName) {
        String apiKey = apiName.name().toLowerCase().replace("_", "-");

        Map<String, String> allPermissions = permissions.get("all");
        if (allPermissions.containsKey(apiKey)) {
            return allPermissions.get(apiKey);
        }

        if (Objects.nonNull(appName) && permissions.containsKey(appName)) {
            Map<String, String> appPermissions = permissions.get(appName);
            if (appPermissions.containsKey(apiKey)) {
                return appPermissions.get(apiKey);
            }
        }

        return null;
    }
}

