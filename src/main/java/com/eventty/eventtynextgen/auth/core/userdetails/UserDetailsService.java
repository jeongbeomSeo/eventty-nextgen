package com.eventty.eventtynextgen.auth.core.userdetails;

public interface UserDetailsService {

    UserDetails loadUserDetailsByLoginId(String loginId);
}
