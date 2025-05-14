package com.eventty.eventtynextgen.certification.authentication;

import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetailsService;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthenticationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginIdPasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        UserDetails userDetails = this.userDetailsService.loadUserDetailsByLoginId(authentication.getUserDetails().getLoginId());

        if (!PasswordEncoder.matches(authentication.getUserDetails().getPassword(), userDetails.getPassword())) {
            throw CustomException.badRequest(AuthenticationErrorType.AUTH_PASSWORD_MISMATCH);
        }

        if (!userDetails.isActive()) {
            throw CustomException.badRequest(CertificationErrorType.AUTH_USER_NOT_ACTIVE);
        }

        return LoginIdPasswordAuthenticationToken.authenticated(userDetails);
    }

}
