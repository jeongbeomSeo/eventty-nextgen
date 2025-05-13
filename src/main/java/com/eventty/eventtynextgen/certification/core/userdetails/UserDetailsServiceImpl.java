package com.eventty.eventtynextgen.certification.core.userdetails;

import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserComponent userComponent;

    @Override
    public UserDetails loadUserDetailsByLoginId(String loginId) {
        User user = this.userComponent.findByEmail(loginId).orElseThrow(
            () -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        return LoginIdUserDetails.fromPrincipal(user.getId(), user.getEmail(), user.getPassword(), user.getUserRole(), user.isDeleted());
    }

}
