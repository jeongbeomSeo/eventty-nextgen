package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantAuthority;
import com.eventty.eventtynextgen.certification.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleAuthorizationProvider implements AuthorizationProvider {

    private final UserComponent userComponent;

    @Override
    public Authentication authorize(Authentication authentication) {
        User user = userComponent.findByUserId(authentication.getUserDetails().getUserId())
            .orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        AuthorizationType authorizationType = AuthorizationType.from(user.getUserRole());

        if (authorizationType == AuthorizationType.NONE) {
            throw CustomException.badRequest(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
        }

        List<GrantAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authorizationType.name()));

        return LoginIdPasswordAuthenticationToken.authorized(authentication, authorities);
    }
}
