package com.eventty.eventtynextgen.base.fixture;

import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import java.util.Set;

public class CertificationTokenFixture {

    public static CertificationTokenInfo createFullAuthorizedCertificationToken() {
        return createCertificationToken("client1", Set.of("user", "events", "auth_code", "auth_login", "auth", "asset_file"));
    }

    public static CertificationTokenInfo createCertificationToken(String appName, Set<String> apiPermission) {
        return JwtTokenProvider.createCertificationToken(appName, apiPermission, 60 * 60 * 1000);
    }

    public static CertificationTokenInfo createExpiredCertificationToken(String appName, Set<String> apiPermission) {
        return JwtTokenProvider.createCertificationToken(appName, apiPermission, -10);
    }
}
