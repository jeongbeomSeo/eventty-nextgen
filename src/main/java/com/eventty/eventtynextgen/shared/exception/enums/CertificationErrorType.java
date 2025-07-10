package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CertificationErrorType implements ErrorType {

    NOT_ALLOWED_APP_NAME("NOT_ALLOWED_APP_NAME", "허용되지 않은 APP NAME입니다."),
    MISMATCH_SECRET_KEY("MISMATCH_SECRET_KEY", "API 토큰 인증키가 일치하지 않습니다."),
    FAILED_PARSING_CERTIFICATION_TOKEN("FAILED_PARSING_CERTIFICATION_TOKEN", "CERTIFICATION TOKEN을 파싱하는데 실패했습니다."),
    MISMATCH_API_NAME("MISMATCH_API_NAME", "요청 URL에 매칭되는 API NAME을 찾을 수 없습니다"),
    NO_API_CALL_PERMISSION_IN_TOKEN("NO_API_CALL_PERMISSION_IN_TOKEN", "요청하신 API 호출 권한이 Token에 담겨있지 않습니다."),
    NO_API_CALL_PERMISSION_IN_YAML("NO_API_CALL_PERMISSION_IN_YAML", "요청하신 API에 대한 호출 권한이 없습니다."),
    ;



    private final String code;
    private final String msg;
}
