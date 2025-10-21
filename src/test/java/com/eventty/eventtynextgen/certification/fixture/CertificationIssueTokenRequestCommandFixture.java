package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.request.CertificationIssueTokenRequestCommand;

public class CertificationIssueTokenRequestCommandFixture {

    public static CertificationIssueTokenRequestCommand create(String appName, String appSecret) {
        return new CertificationIssueTokenRequestCommand(appName, appSecret);
    }

}
