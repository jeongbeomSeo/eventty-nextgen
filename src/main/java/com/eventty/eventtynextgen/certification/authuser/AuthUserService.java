package com.eventty.eventtynextgen.certification.authuser;

import com.eventty.eventtynextgen.certification.authuser.entity.AuthUser;
import com.eventty.eventtynextgen.certification.core.Authentication;
import java.util.Date;

public interface AuthUserService {

    AuthUser saveAuthUser(String sessionId, Date sessionIdExpiredAt, Authentication authentication);
}
