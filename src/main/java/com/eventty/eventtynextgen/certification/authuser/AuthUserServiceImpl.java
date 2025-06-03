package com.eventty.eventtynextgen.certification.authuser;

import com.eventty.eventtynextgen.certification.authuser.entity.AuthUser;
import com.eventty.eventtynextgen.certification.authuser.repository.AuthUserRepository;
import com.eventty.eventtynextgen.shared.utils.DateUtils;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;

    @Override
    @Transactional
    public AuthUser saveAuthUser(String sessionId, Date sessionIdExpiredAt, Long userId, String joinedAuthorities) {
        return this.authUserRepository.findById(userId).map(authUser -> {
            authUser.updateAuthUserInfo(joinedAuthorities, sessionId, DateUtils.convertFormatToLocalDateTime(sessionIdExpiredAt));
            return authUser;
        }).orElseGet(() -> {
            AuthUser authUser = AuthUser.of(userId, joinedAuthorities, sessionId, DateUtils.convertFormatToLocalDateTime(sessionIdExpiredAt));
            return this.authUserRepository.save(authUser);
        });
    }
}
