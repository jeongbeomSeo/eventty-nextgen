package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.auth.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.auth.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.auth.core.userdetails.LoginIdUserDetails;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthenticationProvider authenticationProvider;
    private final AuthorizationProvider authorizationProvider;
    private final UserComponent userComponent;

    @Override
    public Authentication authenticateAndAuthorize(String loginId, String password) {
        // 1. 사용자 검증
        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        // 2. 사용자 역할 확인 및 권한 할당
        return this.authorizationProvider.authorize(authenticate);
    }

    @Override
    public User getActivatedUser(Long userId) {
        User user = this.userComponent.getUserByUserId(userId)
            .orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
        }

        return user;
    }

    @Override
    public String getJoinedAuthoritiesByUserRole(UserRoleType userRoleType) {
        List<GrantedAuthority> authorities = this.authorizationProvider.getAuthoritiesByUserRole(userRoleType);
        return this.joinAuthorities(authorities);
    }

    @Override
    public String joinAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities.isEmpty()) {
            return null;
        }

        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }
}
