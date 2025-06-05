package com.eventty.eventtynextgen.auth.authorization;

import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.List;

public interface AuthorizationProvider {

    Authentication authorize(Authentication authentication);

    List<GrantedAuthority> getAuthoritiesByUserRole(UserRoleType userRole);
}
