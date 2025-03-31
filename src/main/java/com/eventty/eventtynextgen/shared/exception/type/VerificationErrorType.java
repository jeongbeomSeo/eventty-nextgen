package com.eventty.eventtynextgen.shared.exception.type;

import com.eventty.eventtynextgen.shared.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationErrorType implements ErrorType {

    CERTIFICATION_CODE_SAVE_ERROR("CERTIFICATION_CODE_SAVE_ERROR", "인증 코드 저장 중 오류가 발생했습니다."),
    EXPIRE_EMAIL_VERIFICATION_CODE("EXPIRE_EMAIL_VERIFICATION",
        "이메일 검증 코드가 만료되었습니다."),
    MISMATCH_EMAIL_VERIFICATION_CODE("MISMATCH_EMAIL_VERIFICATION_CODE",
        "이메일 검증 코드가 일치하지 않습니다."),
    ;

    private final String code;
    private final String msg;
    }
