package com.eventty.eventtynextgen.shared.context;

import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import java.util.Map;
import lombok.Getter;

@Getter
public class CertificationContext {

    private boolean skipCertificate;
    private String appName;
    private Map<String, Permission> apiPermissionMap;
    private String adminEmail;
    private String tokenParsingFailureReason;

    public CertificationContext() {
        this.skipCertificate = false;
    }

    public void markCertificateAsSkipped() {
        this.skipCertificate = true;
    }

    public void updateFromTokenClaims(String appName, Map<String, Permission> apiPermissionMap, String adminEmail) {
        this.appName = appName;
        this.apiPermissionMap = apiPermissionMap;
        this.adminEmail = adminEmail;
    }

    public void updateTokenParsingFailureReason(String failureReason) {
        this.tokenParsingFailureReason = failureReason;
    }
}
