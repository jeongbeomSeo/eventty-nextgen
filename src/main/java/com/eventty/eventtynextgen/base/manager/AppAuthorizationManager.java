package com.eventty.eventtynextgen.base.manager;

import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.AppPermission;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppAuthorizationManager {

    private final AuthorizationApiProperties authorizationApiProperties;

    public Permission getApiPermission(String appName, String apiName) {
        Map<String, AppPermission> infoMap = authorizationApiProperties.getInfoMap();

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
        Map<String, AppPermission> infoMap = authorizationApiProperties.getInfoMap();

        if (!this.containsAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_ALLOW_APP_NAME);
        }

        return infoMap.get(appName).getAppPermissions();
    }
    public boolean containsAppName(String appName) {
        Map<String, AppPermission> infoMap = authorizationApiProperties.getInfoMap();

        return infoMap.containsKey(appName);
    }
}
