package com.eventty.eventtynextgen.certification.authuser;

import com.eventty.eventtynextgen.certification.authuser.entity.AuthUser;
import com.eventty.eventtynextgen.certification.authuser.repository.AuthUserRepository;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.utils.DateUtils;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    // TODO: 리팩토링 하는 과정에서 AuthenticationProvider, AuthorizationProvider를 해당 객체로 옮겨서 처리
    // TODO: Provider의 역할과 메서드의 책임을 고려하여 사용자 인증 과정에서 사용하는 사용자 호출 로직이나 사용자에게 권한 인가 과정에서 발생하는 검증 처리는 상위 메서드에서 처리

    @Override
    public AuthUser saveAuthUser(String sessionId, Date sessionIdExpiredAt, Authentication authentication) {
        if (!authentication.isAuthorized()) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorType.INVALID_INPUT_DATA,
                "Auth User를 생성하기 위해서는 인증·인가 작업이 완료된 Authentication 객체가 필요합니다.");
        }

        AuthUser authUser = AuthUser.of(authentication.getUserDetails().getUserId(), authentication.getUserDetails().getUserRole().name(), sessionId,
            DateUtils.convertFormatToLocalDateTime(sessionIdExpiredAt));

        return this.authUserRepository.save(authUser);
    }
}
