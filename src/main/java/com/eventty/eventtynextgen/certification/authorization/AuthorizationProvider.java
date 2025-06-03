package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.List;

public interface AuthorizationProvider {

    Authentication authorize(Authentication authentication);

    List<GrantedAuthority> getAuthoritiesByUserRole(UserRoleType userRole);
}
