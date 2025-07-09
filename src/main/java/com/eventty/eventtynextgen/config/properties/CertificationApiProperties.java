package com.eventty.eventtynextgen.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "certification")
public class CertificationApiProperties {

    private Map<String, ApiPermission> infoMap;

    @Getter
    @Setter
    public static class ApiPermission {
        private Map<String, Permission> apiPermissions;
    }

    @Getter
    public enum Permission {
        FREE,
        LOGIN,
        OPTIONAL
    }
}

