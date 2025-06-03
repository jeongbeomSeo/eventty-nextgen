package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
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
            throw CustomException.badRequest(CertificationErrorType.NOT_ALLOWED_AUTHORIZE_WITHOUT_AUTHENTICATION);
        }
        List<GrantedAuthority> authorities = this.getAuthoritiesByUserRole(authentication.getUserDetails().getUserRole());

        return LoginIdPasswordAuthenticationToken.authorized(authentication, authorities);
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesByUserRole(UserRoleType userRole) {
        AuthorizationType authorizationType = AuthorizationType.from(userRole);

        if (authorizationType == AuthorizationType.NONE) {
            throw CustomException.badRequest(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
        }

        return Collections.singletonList(new SimpleGrantedAuthority(authorizationType.name()));
    }
}
