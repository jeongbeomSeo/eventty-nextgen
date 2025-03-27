package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.user.model.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.user.model.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.user.model.response.EmailVerificationResponse;

public interface VerificationService {

    /**
     * 사용자의 인증 정보(예, 이메일, 핸드폰 번호 등)가 DB에 이미 존재하는지 확인한다.
     *
     * @param credential 검증 대상 값 (현재는 이메일이지만, 추후 다른 값도 될 수 있음)
     * @return 이미 존재하면 true, 없으면 false
     */
    boolean existsCredential(String credential);

    /**
     * 주어진 인증 대상(예, 이메일)에 대해 인증 코드를 생성하고 전송한다.
     *
     * @param request 인증 코드 전송 요청 정보 (현재는 이메일 전송 요청)
     * @return 전송 결과에 대한 응답 객체
     */
    EmailVerificationResponse sendVerificationCode(EmailVerificationRequest request);

    /**
     * 사용자가 입력한 인증 코드가 서버에 저장된 값과 일치하는지 검증한다.
     *
     * @param request 인증 코드 검증 요청 정보
     * @return 일치하면 true, 불일치하면 false
     */
    boolean validateVerificationCode(EmailVerificationValidationRequest request);
}
