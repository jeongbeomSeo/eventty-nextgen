package com.eventty.eventtynextgen.shared.context;

import java.util.Set;
import lombok.Getter;

@Getter
public class CertificationContext {

    private boolean skipCertificate;
    private String appName;
    private Set<String> apiPermission;
    private String adminEmail;
    private String tokenParsingFailureReason;

    public CertificationContext() {
        this.skipCertificate = false;
    }

    public void markCertificateAsSkipped() {
        this.skipCertificate = true;
    }

    public void updateFromTokenClaims(String appName, Set<String> apiPermission, String adminEmail) {
        this.appName = appName;
        this.apiPermission = apiPermission;
        this.adminEmail = adminEmail;
    }

    public void updateTokenParsingFailureReason(String failureReason) {
        this.tokenParsingFailureReason = failureReason;
    }
}
