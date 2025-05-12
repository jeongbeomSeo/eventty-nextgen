package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;

public class UserDetailsFixture {

    public static UserDetails createLoginIdUserDetailsFromCredentials() {
        return LoginIdUserDetails.fromCredentials("example@gmail.com", "password");
    }

    public static UserDetails createLoginIdUserDetailsFromPrincipal() {
        return LoginIdUserDetails.fromPrincipal(1L, "example@gmail.com", PasswordEncoder.encode("password"), false);
    }
}
