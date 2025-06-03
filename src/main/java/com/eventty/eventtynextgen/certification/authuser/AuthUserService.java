package com.eventty.eventtynextgen.certification.authuser;

import com.eventty.eventtynextgen.certification.authuser.entity.AuthUser;
import java.util.Date;

public interface AuthUserService {

    AuthUser saveAuthUser(String sessionId, Date sessionIdExpiredAt, Long userId, String joinedAuthorities);
}
