package com.eventty.eventtynextgen.base.fixture;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import java.util.Map;

public class CertificationTokenFixture {

    public static CertificationTokenInfo createCertificationToken(String appName, Map<String, Permission> apiPermissionMap) {
        return JwtTokenProvider.createCertificationToken(appName, apiPermissionMap, 60 * 60 * 1000);
    }

    public static CertificationTokenInfo createExpiredCertificationToken(String appName, Map<String, Permission> apiPermissionMap) {
        return JwtTokenProvider.createCertificationToken(appName, apiPermissionMap, -10);
    }
}
