package com.eventty.eventtynextgen.certification.service;

import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collection;

public interface AuthService {

    Authentication authenticateAndAuthorize(String loginId, String password);

    User getActivatedUser(Long userId);

    String getJoinedAuthoritiesByUserRole(UserRoleType userRoleType);

    String joinAuthorities (Collection<? extends GrantedAuthority> authorities);
}
