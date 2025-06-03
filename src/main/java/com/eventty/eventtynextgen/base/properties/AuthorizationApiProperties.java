package com.eventty.eventtynextgen.base.properties;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "authorization")
public class AuthorizationApiProperties {

    private Map<String, AppPermission> infoMap;

    @Getter
    @Setter
    public static class AppPermission {
        private Map<String, Permission> appPermissions;

        public boolean containsApiPermission(String apiName) {
            return this.appPermissions.containsKey(apiName);
        }
    }

    @Getter
    public enum Permission {
        FREE,
        LOGIN,
        OPTIONAL
    }
}

