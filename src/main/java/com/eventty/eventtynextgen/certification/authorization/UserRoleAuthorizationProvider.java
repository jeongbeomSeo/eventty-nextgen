package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleAuthorizationProvider implements AuthorizationProvider {

    @Override
    public Authentication authorize(Authentication authentication) {
        AuthorizationType authorizationType = AuthorizationType.from(authentication.getUserDetails().getUserRole());

        if (canAuthorize(authentication, authorizationType)) {
            throw CustomException.badRequest(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authorizationType.name()));

        return LoginIdPasswordAuthenticationToken.authorized(authentication, authorities);
    }

    private boolean canAuthorize(Authentication authentication, AuthorizationType authorizationType) {
        return !authentication.isAuthenticated() || authorizationType == AuthorizationType.NONE;
    }
}
