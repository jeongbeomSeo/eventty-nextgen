package com.eventty.eventtynextgen.certification.component;

import com.eventty.eventtynextgen.config.properties.CertificationApiProperties;
import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import com.eventty.eventtynextgen.config.properties.CertificationSecretProperties;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificationManager {

    private final CertificationSecretProperties certificationSecretProperties;
    private final CertificationApiProperties certificationApiProperties;

    public boolean hasAppName(String appName) {
        return this.certificationSecretProperties.getCertificationSecrets().containsKey(appName) && this.certificationApiProperties.getInfoMap().containsKey(appName);
    }

    public Map<String, Permission> findApiPermission(String appName) {
        if (hasAppName(appName)) {
            return this.certificationApiProperties.getInfoMap().get(appName).getApiPermissions();
        }
        return Collections.emptyMap();
    }

    public boolean matchesSecretKey(String appName, String appSecret) {
        if (hasAppName(appName)) {
            return this.certificationSecretProperties.getCertificationSecrets().get(appName).equals(appSecret);
        }
        return false;
    }
}
