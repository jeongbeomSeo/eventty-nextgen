package com.eventty.eventtynextgen.base.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.certification.component.CertificationManager;
import com.eventty.eventtynextgen.shared.context.CertificationContext;
import com.eventty.eventtynextgen.shared.context.CertificationContextHolder;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.base.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Api Permission Verified Filter 단위 테스트")
class ApiPermissionVerifiedFilterTest {

    @Mock
    private CertificationManager certificationManager;

    @Mock
    private ResponseUtils responseUtils;

    @BeforeEach
    void tearDown() {
        CertificationContextHolder.clearContext();
        SessionContextHolder.clearContext();
    }

    @Test
    @DisplayName("API 호출 권한 검증을 SKIP 해야하는 경우 곧바로 다음 필터로 넘어간다")
    void API_호출_권한_검증을_SKIP_해야하는_경우_곧바로_다음_필터로_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContextHolder.getContext().markCertificateAsSkipped();

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("모든 검증을 성공적으로 통과하면 다음 필터로 넘어간다")
    void 모든_검증을_성공적으로_통과하면_다음_필터로_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContext context = CertificationContextHolder.getContext();
        context.updateFromTokenClaims("client", Set.of("user"), "adminEmail@gmail.com");
        when(request.getRequestURI()).thenReturn("/api/v1/user");
        when(certificationManager.findApiPermission(context.getAppName())).thenReturn(Set.of("user"));

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Certification Token의 페이로드가 Context에 반영되어 있지 않을 경우 요청은 필터링된다")
    void Certification_Token의_페이로드가_Context에_반영되어_있지_않을_경우_요청을_필터링한다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContext context = CertificationContextHolder.createEmptyContext();
        CertificationContextHolder.setContext(context);

        doNothing().when(responseUtils).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @ParameterizedTest(name = "[{index}] URI: {0}")
    @MethodSource("mismatchUriPatternArguments")
    @DisplayName("요청의 Path로부터 API Name을 찾지 못했을 경우 요청은 필터링된다.")
    void 요청의_Path로부터_API_Name을_찾지_못했을_경우_요청은_필터링된다(String fixtureName, String requestURI) throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContext context = CertificationContextHolder.getContext();
        context.updateFromTokenClaims("client", Set.of("user"), "adminEmail@gmail.com");

        when(request.getRequestURI()).thenReturn(requestURI);
        doNothing().when(responseUtils).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    private static Stream<Arguments> mismatchUriPatternArguments() {
        return Stream.of(
            // 완전히 다른 패턴
            Arguments.of("완전히 다른 패턴", "/api/v2/user"),
            Arguments.of("완전히 다른 패턴", "/different/path"),

            // 추가 경로가 있지만 패턴에 맞지 않는 경우
            Arguments.of("패턴 뒤에 슬래시가 아닌 문자열이 온 경우", "/api/v1/userses"),
            Arguments.of("패턴 뒤에 슬래시가 아닌 문자열이 온 경우", "/api/v1/auths"),

            // API 버전이 다른 경우
            Arguments.of("API 버전 불일치", "/api/v2/events"),
            Arguments.of("API 버전 불일치", "/api/v0/auth"),

            // 오타가 있는 경우
            Arguments.of("오타", "/api/v1/usr"),
            Arguments.of("오타", "/api/v1/event"), // events의 단수형
            Arguments.of("오타", "/api/v1/eventss"), // 이중 's'
            Arguments.of("오타", "/api/v1/asset/files"), // file의 복수형

            // 패스 구조가 다른 경우
            Arguments.of("패스 구조 불일치", "/user/api/v1"),
            Arguments.of("패스 구조 불일치", "/v1/api/user"),
            Arguments.of("패스 구조 불일치", "/api/user/v1"),

            // 빈 문자열이나 null 케이스
            Arguments.of("빈 문자열", ""),
            Arguments.of("루트 패스", "/"),

            // 대소문자 다른 경우
            Arguments.of("대소문자 불일치", "/API/V1/USER"),
            Arguments.of("대소문자 불일치", "/api/V1/events"),
            Arguments.of("대소문자 불일치", "/Api/v1/auth"),

            // 슬래시 누락 케이스
            Arguments.of("슬래시 누락", "api/v1/user"),
            Arguments.of("슬래시 누락", "/apiv1/user"),
            Arguments.of("슬래시 누락", "/api/v1user")
        );
    }

    @Test
    @DisplayName("Token에 저장되어 있는 API 호출 권한을 검사한 결과가 실패인 경우 요청은 필터링된다")
    void Token에_저장되어_있는_API_호출_권한을_검사한_결과가_실패인_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContext context = CertificationContextHolder.getContext();
        context.updateFromTokenClaims("client", Set.of("auth"), "adminEmail@gmail.com");
        when(request.getRequestURI()).thenReturn("/api/v1/user");
        doNothing().when(responseUtils).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    @DisplayName("YAML에 저장되어 있는 API 호출 권한을 검사한 결과가 실패인 경우 요청은 필터링된다")
    void YAML에_저장되어_있는_API_호출_권한을_검사한_결과가_실패인_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        CertificationContext context = CertificationContextHolder.getContext();
        context.updateFromTokenClaims("client", Set.of("user"), "adminEmail@gmail.com");
        when(request.getRequestURI()).thenReturn("/api/v1/user");
        when(certificationManager.findApiPermission(context.getAppName())).thenReturn(Set.of("auth"));
        doNothing().when(responseUtils).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));

        ApiPermissionVerifiedFilter apiPermissionVerifiedFilter = new ApiPermissionVerifiedFilter(certificationManager, responseUtils);

        // when
        apiPermissionVerifiedFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }
}