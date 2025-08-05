package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.fixture.CertificationTokenFixture;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.shared.context.CertificationContext;
import com.eventty.eventtynextgen.shared.context.CertificationContextHolder;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Certification Token Filter 단위 테스트")
class CertificationTokenFilterTest {

    @BeforeEach
    void tearDown() {
        CertificationContextHolder.clearContext();
        SessionContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 Certification Token과 함께 요청이 들어올 경우, filterChain.doFilter() 직전에 Context에 페이로드 정보가 저장된다")
    void 유효한_Certification_Token과_함께_요청이_들어올_경우_Context에_페이로드_정보가_저장되고_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Set<String> apiPermission = Set.of("user");
        CertificationTokenInfo tokenInfo = CertificationTokenFixture.createCertificationToken("client", apiPermission);
        String rawToken = tokenInfo.getCertificationToken();

        when(request.getRequestURI()).thenReturn("/api/v1/user");
        when(request.getHeader(CERTIFICATION_TOKEN_COOKIE_NAME)).thenReturn(rawToken);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            CertificationContext context = CertificationContextHolder.getContext();
            assertThat(context.getAppName()).isEqualTo("client");
            assertThat(context.getApiPermission().contains("user")).isEqualTo(true);
            assertThat(context.getAdminEmail()).isNotBlank();
            assertThat(context.isSkipCertificate()).isFalse();
            assertThat(context.getTokenParsingFailureReason()).isNull();

            return null;
        }).when(filterChain).doFilter(request, response);

        CertificationTokenFilter certificationTokenFilter = new CertificationTokenFilter();

        // when
        certificationTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(CertificationContextHolder.getContext().getAppName()).isNull();
        assertThat(CertificationContextHolder.getContext().getApiPermission()).isNull();
        assertThat(CertificationContextHolder.getContext().getAdminEmail()).isNull();
    }

    @Test
    @DisplayName("스킵 URL이 요청의 PATH로 들어올 경우 Context에 Skip 마킹이 되고 넘어간다")
    void 스킵_URL이_요청의_PATCH로_들어올_경우_Context에_SKIP_마킹이_되고_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/swagger-ui");

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            CertificationContext context = CertificationContextHolder.getContext();
            assertThat(context.getAppName()).isNull();
            assertThat(context.getApiPermission()).isNull();
            assertThat(context.getAdminEmail()).isNull();
            assertThat(context.isSkipCertificate()).isTrue();
            assertThat(context.getTokenParsingFailureReason()).isNull();

            return null;
        }).when(filterChain).doFilter(request, response);

        CertificationTokenFilter certificationTokenFilter = new CertificationTokenFilter();

        // when
        certificationTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(CertificationContextHolder.getContext().getAppName()).isNull();
        assertThat(CertificationContextHolder.getContext().getApiPermission()).isNull();
        assertThat(CertificationContextHolder.getContext().getAdminEmail()).isNull();
    }

    @Test
    @DisplayName("요청에 토큰이 존재하지 않을 경우 Context에 페이로드 정보는 null로 남고 넘어간다")
    void 요청에_토큰이_존재하지_않을_경우_Context에_페이로드_정보는_null로_남고_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/user");
        when(request.getHeader(CERTIFICATION_TOKEN_COOKIE_NAME)).thenReturn(null);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            CertificationContext context = CertificationContextHolder.getContext();
            assertThat(context.getAppName()).isNull();
            assertThat(context.getApiPermission()).isNull();
            assertThat(context.getAdminEmail()).isNull();
            assertThat(context.isSkipCertificate()).isFalse();
            assertThat(context.getTokenParsingFailureReason()).isNull();

            return null;
        }).when(filterChain).doFilter(request, response);

        CertificationTokenFilter certificationTokenFilter = new CertificationTokenFilter();

        // when
        certificationTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(CertificationContextHolder.getContext().getAppName()).isNull();
        assertThat(CertificationContextHolder.getContext().getApiPermission()).isNull();
        assertThat(CertificationContextHolder.getContext().getAdminEmail()).isNull();
    }

    @Test
    @DisplayName("토큰의 유효성 검사가 실패했다면 Context에 실패 사유를 남기고 넘어간다")
    void 토큰의_유효성_검사가_실패했다면_Context에_실패_사유를_남기고_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Set<String> apiPermission = Set.of("user");
        CertificationTokenInfo expiredToken = CertificationTokenFixture.createExpiredCertificationToken("client", apiPermission);

        when(request.getRequestURI()).thenReturn("/api/v1/user");
        when(request.getHeader(CERTIFICATION_TOKEN_COOKIE_NAME)).thenReturn(expiredToken.getCertificationToken());

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            CertificationContext context = CertificationContextHolder.getContext();
            assertThat(context.getAppName()).isNull();
            assertThat(context.getApiPermission()).isNull();
            assertThat(context.getAdminEmail()).isNull();
            assertThat(context.isSkipCertificate()).isFalse();
            assertThat(context.getTokenParsingFailureReason()).isEqualTo(VerifyTokenResult.EXPIRED_TOKEN.name());

            return null;
        }).when(filterChain).doFilter(request, response);

        CertificationTokenFilter certificationTokenFilter = new CertificationTokenFilter();

        // when
        certificationTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(CertificationContextHolder.getContext().getAppName()).isNull();
        assertThat(CertificationContextHolder.getContext().getApiPermission()).isNull();
        assertThat(CertificationContextHolder.getContext().getAdminEmail()).isNull();
    }
}