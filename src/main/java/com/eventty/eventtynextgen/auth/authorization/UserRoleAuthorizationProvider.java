package com.eventty.eventtynextgen.auth.authorization;

import com.eventty.eventtynextgen.auth.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.auth.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.auth.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleAuthorizationProvider implements AuthorizationProvider {

    @Override
    public Authentication authorize(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw CustomException.badRequest(AuthErrorType.NOT_ALLOWED_AUTHORIZE_WITHOUT_AUTHENTICATION);
        }
        List<GrantedAuthority> authorities = this.getAuthoritiesByUserRole(authentication.getUserDetails().getUserRole());

        return LoginIdPasswordAuthenticationToken.authorized(authentication, authorities);
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesByUserRole(UserRoleType userRole) {
        AuthorizationType authorizationType = AuthorizationType.from(userRole);

        if (authorizationType == AuthorizationType.NONE) {
            throw CustomException.badRequest(AuthErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
        }

        return Collections.singletonList(new SimpleGrantedAuthority(authorizationType.name()));
    }
}
