package com.eventty.eventtynextgen.certification.component;

import com.eventty.eventtynextgen.config.properties.CertificationApiProperties;
import com.eventty.eventtynextgen.config.properties.CertificationSecretProperties;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificationManager {

    private final CertificationSecretProperties certificationSecretProperties;
    private final CertificationApiProperties certificationApiProperties;

    public boolean hasAppName(String appName) {
        return this.certificationSecretProperties.getCertificationSecrets().containsKey(appName) && this.certificationApiProperties.getApiPermissionMap().containsKey(appName);
    }

    public Set<String> findApiPermission(String appName) {
        return this.certificationApiProperties.getApiPermissionMap().get(appName);
    }

    public boolean matchesSecretKey(String appName, String appSecret) {
        if (hasAppName(appName)) {
            return this.certificationSecretProperties.getCertificationSecrets().get(appName).equals(appSecret);
        }
        return false;
    }
}
