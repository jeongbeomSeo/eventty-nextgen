package com.eventty.eventtynextgen.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_VALIDITY_IN_MS;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.certification.component.CertificationManager;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationManager certificationManager;

    @Override
    public void issueCertificationToken(String appName, String appSecret, HttpServletResponse response) {
        // 1. app Name 존재하는지 확인
        if (!this.certificationManager.hasAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_ALLOWED_APP_NAME);
        }

        // 2. app Secret 비교
        if (!this.certificationManager.matchesSecretKey(appName, appSecret)) {
            throw CustomException.badRequest(CertificationErrorType.MISMATCH_SECRET_KEY);
        }

        // 3. Api Allow Set 가져오기
        Set<String> apiPermission = this.certificationManager.findApiPermission(appName);
        if (apiPermission.isEmpty()) {
            log.warn("appName의 API 호출 권한 리스트가 빈 상태로 발급되었습니다. appName: {}", appName);
        }

        // 4. Certification Token 발급
        CertificationTokenInfo certificationToken = JwtTokenProvider.createCertificationToken(appName, apiPermission, CERTIFICATION_TOKEN_VALIDITY_IN_MS);

        // 5. 생성한 토큰 쿠키에 담기
        CookieUtils.addLaxCookie(CERTIFICATION_TOKEN_COOKIE_NAME, certificationToken.getCertificationToken(), CERTIFICATION_TOKEN_VALIDITY_IN_MS / 1000,
            response);
    }
}
