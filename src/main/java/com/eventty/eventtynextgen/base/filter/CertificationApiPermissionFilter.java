package com.eventty.eventtynextgen.base.filter;

import com.eventty.eventtynextgen.base.enums.ApiName;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.certification.component.CertificationManager;
import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import com.eventty.eventtynextgen.shared.context.CertificationContext;
import com.eventty.eventtynextgen.shared.context.CertificationContextHolder;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-2)
@RequiredArgsConstructor
@Component
public class CertificationApiPermissionFilter extends OncePerRequestFilter {

    private final CertificationManager certificationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Context.isSkipCertificate 값z이 true일 경우 다음 Filter로 넘긴다
        CertificationContext context = CertificationContextHolder.getContext();
        if (context.isSkipCertificate()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Context에 토큰을 파싱한 결과가 업데이트되어 있지 않은 경우 요청을 필터링하여 예외 메시지를 전달한다
        if (!verifyTokenParsed(context, response)) return;

        // 3. 요청의 PATH를 통해 APIName을 가져온다.
        ApiName apiName = resolveApiName(request.getRequestURI(), response);
        if (Objects.isNull(apiName)) {
            return;
        }

        // 4. 1차 검증 - 요청의 PATH와 토큰의 Api Allow Map(Context)의 매칭 검사를 한다
        if (!validateApiPermissionInToken(apiName, context, response)) {
            return;
        }

        // 5. 2차 검증 - 요청의 PATH와 설정 파일의 Api Allow의 매칭 검사를 한다
        if (!validateApiPermissionByYaml(apiName, context.getAppName(), response)) {
            return;
        }

        // 6. Permission을 확인하여 FREE, OPTIONAL인 경우 SessionContext의 SkipSession을 true로 마킹한다
        if (shouldSkipSessionCheck(apiName, context.getApiPermissionMap())) {
            SessionContextHolder.getContext().markSessionCheckAsSkipped();
        }

        filterChain.doFilter(request, response);
    }
    private boolean verifyTokenParsed(CertificationContext context, HttpServletResponse response) {
        if (!isTokenPayloadUpdatedInContext(context)) {
            String tokenParsingFailureReason = context.getTokenParsingFailureReason();

            CustomException customException = CustomException.badRequest(CertificationErrorType.FAILED_PARSING_CERTIFICATION_TOKEN,
                "Reason: " + tokenParsingFailureReason);
            writeErrorResponse(customException, response);

            return false;
        }

        return true;
    }

    private boolean isTokenPayloadUpdatedInContext(CertificationContext context) {
        return StringUtils.hasText(context.getAppName()) && Objects.nonNull(context.getApiPermissionMap());
    }

    private ApiName resolveApiName(String requestURI, HttpServletResponse response) {
        Optional<ApiName> apiNameOpt = Arrays.stream(ApiName.values())
            .filter(apiName -> requestURI.startsWith(apiName.getPattern()))
            .findAny();

        if (apiNameOpt.isEmpty()) {
            CustomException customException = CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CertificationErrorType.MISMATCH_API_NAME);
            writeErrorResponse(customException, response);

            return null;
        }

        return apiNameOpt.get();
    }
    private boolean validateApiPermissionInToken(ApiName apiName, CertificationContext context, HttpServletResponse response) {
        Map<String, Permission> apiPermissionByToken = context.getApiPermissionMap();
        String key = apiName.name().toLowerCase();

        if (!apiPermissionByToken.containsKey(key)) {
            CustomException customException = CustomException.of(HttpStatus.FORBIDDEN, CertificationErrorType.NO_API_CALL_PERMISSION_IN_TOKEN);
            writeErrorResponse(customException, response);
            return false;
        }

        return true;
    }

    private boolean validateApiPermissionByYaml(ApiName apiName, String appName, HttpServletResponse response) {
        Map<String, Permission> apiPermissionByYaml = this.certificationManager.findApiPermission(appName);
        String key = apiName.name().toLowerCase();

        if (!apiPermissionByYaml.containsKey(key)) {
            CustomException customException = CustomException.of(HttpStatus.FORBIDDEN, CertificationErrorType.NO_API_CALL_PERMISSION_IN_YAML);
            writeErrorResponse(customException, response);
            return false;
        }

        return true;
    }
    public boolean shouldSkipSessionCheck(ApiName apiName, Map<String, Permission> apiPermissionMap) {
        Permission permission = apiPermissionMap.get(apiName.name().toLowerCase());
        return permission != Permission.LOGIN;
    }

    private void writeErrorResponse(CustomException customException, HttpServletResponse response) {
        log.error("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            customException.getDetail());

        ResponseUtils.writeErrorResponseToResponse(response, customException);
    }
}
