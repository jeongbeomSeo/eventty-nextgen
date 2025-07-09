package com.eventty.eventtynextgen.certification;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.certification.component.CertificationManager;
import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.ApiPermission;
import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationServiceImpl 단위 테스트")
class CertificationServiceImplTest {

    @Mock
    private CertificationManager certificationManager;

    @Nested
    @DisplayName("API 호출 권한인 Certification Token 발행 테스트")
    class IssueCertificationToken {

        @Test
        @DisplayName("인자로 들어온 appName과 appSecret을 통한 유효성 검증에 통과한다면 토큰 발급에 성공한다")
        void 인자로_들어온_값들을_통한_유효성_검증에_통과한다면_토큰_발급에_성공한다() {
            // given
            String appName = "client";
            String appSecret = "secretKey";
            HttpServletResponse response = mock(HttpServletResponse.class);
            Map<String, Permission> apiPermissions = Map.of("user", Permission.OPTIONAL);

            when(certificationManager.hasAppName(appName)).thenReturn(true);
            when(certificationManager.matchesSecretKey(appName, appSecret)).thenReturn(true);
            when(certificationManager.findApiPermission(appName)).thenReturn(apiPermissions);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(certificationManager);

            // when
            CertificationIssueCertificationTokenResponseView result = certificationService.issueCertificationToken(
                appName, appSecret, response);

            // then
            assertThat(result.certificationToken().getCertificationToken()).isNotBlank();
            assertThat(result.certificationToken().getTokenType()).isNotBlank();
        }

        @Test
        @DisplayName("인자로 들어온 값들의 유효성 검증에 통과하지만 API 호출 권한이 빈 상태라면 발급에 성공하지만 에러 로그를 남긴다")
        void 인자로_들어온_값들의_유효성_검증에_통과하지만_API_호출_권한이_빈_상태라면_발급에_성공하지만_에러_로그를_남긴다() {
            // given
            String appName = "client";
            String appSecret = "secretKey";
            HttpServletResponse response = mock(HttpServletResponse.class);
            Map<String, Permission> apiPermissions = Collections.emptyMap();

            when(certificationManager.hasAppName(appName)).thenReturn(true);
            when(certificationManager.matchesSecretKey(appName, appSecret)).thenReturn(true);
            when(certificationManager.findApiPermission(appName)).thenReturn(apiPermissions);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(certificationManager);

            // when
            CertificationIssueCertificationTokenResponseView result = certificationService.issueCertificationToken(
                appName, appSecret, response);

            // then
            assertThat(result.certificationToken().getCertificationToken()).isNotBlank();
            assertThat(result.certificationToken().getTokenType()).isNotBlank();
        }

        @Test
        @DisplayName("인자로 들어온 appName이 yaml 파일에 존재하지 않는다면 발급에 실패한다")
        void 인자로_들어온_appName이_yaml_파일에_존재하지_않는다면_발급에_실패한다() {
            // given
            String appName = "client";
            String appSecret = "secretKey";
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(certificationManager.hasAppName(appName)).thenReturn(false);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(certificationManager);

            // when & then
            assertThatThrownBy(() -> certificationService.issueCertificationToken(appName, appSecret, response))
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(CertificationErrorType.NOT_ALLOWED_APP_NAME);
                });
        }

        @Test
        @DisplayName("인자로 들어온 appSecret의 비교 검증이 실패한다면 토큰 발급에 실패한다")
        void 인자로_들어온_appSecret의_비교_검증이_실패한다면_토큰_발급에_실패한다() {
            // given
            String appName = "client";
            String appSecret = "secretKey";
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(certificationManager.hasAppName(appName)).thenReturn(true);
            when(certificationManager.matchesSecretKey(appName, appSecret)).thenReturn(false);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(certificationManager);

            // when & then
            assertThatThrownBy(() -> certificationService.issueCertificationToken(appName, appSecret, response))
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(400);
                    assertThat(customException.getErrorType()).isEqualTo(CertificationErrorType.MISMATCH_SECRET_KEY);
                });
        }
    }
}