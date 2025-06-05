package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collection;

public interface AuthUserService {

    Authentication authenticateAndAuthorize(String loginId, String password);

    User getActivatedUser(Long userId);

    String getJoinedAuthoritiesByUserRole(UserRoleType userRoleType);

    String joinAuthorities (Collection<? extends GrantedAuthority> authorities);
}
