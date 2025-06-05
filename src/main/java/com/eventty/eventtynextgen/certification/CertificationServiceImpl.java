package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.base.manager.AppAuthorizationManager;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private AppAuthorizationManager appAuthorizationManager;

    @Override
    public CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName) {
        // 1. 해당 appName properties에 존재하는지 확인
        if (!this.appAuthorizationManager.containsAppName(appName)) {
            throw CustomException.badRequest(AuthErrorType.NOT_FOUND_APP_NAME);
        }

        // TODO: APP TOKEN도 인자로 받아서 다른 properties를 통해 App Name에 해당하는 App Token값을 가져와서 값 매칭 검증

        // 2. API Allow 리스트를 조회
        Map<String, Permission> apiPermissions = this.appAuthorizationManager.getPermissions(appName);

        // 3. Certification Token을 발급 // TODO: Certification Token을 만들어주는 메서드 구현 및 parsing 함수도 구현
//        CertificationTokenInfo certificationToken = this.tokenService.issueCertificationToken(appName, apiPermissions);

        // TODO: 생성한 토큰 반환하는 것이 아닌 HTTP Only 헤더에 담기도록 구현하고, 매 요청마다 담겨오도록 구성 (Header Name: certification)
        return new CertificationIssueCertificationTokenResponseView(null);
    }

}
