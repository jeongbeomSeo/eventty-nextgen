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

    public Permission getApiPermission(String appName, String apiName) {
        if (!this.containsAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_ALLOW_APP_NAME);
        }

        AppPermission appPermission = infoMap.get(appName);
        if (!appPermission.containsApiPermission(apiName)) {
            throw CustomException.of(HttpStatus.valueOf(500), CertificationErrorType.NOT_REGISTERED_API_NAME);
        }

        return appPermission.getAppPermissions().get(apiName);
    }

    public Map<String, Permission> getPermissions(String appName) {
        if (!this.containsAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_ALLOW_APP_NAME);
        }

        return infoMap.get(appName).getAppPermissions();
    }
    public boolean containsAppName(String appName) {
        return infoMap.containsKey(appName);
    }

    @Getter
    @Setter
    private static class AppPermission {
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

