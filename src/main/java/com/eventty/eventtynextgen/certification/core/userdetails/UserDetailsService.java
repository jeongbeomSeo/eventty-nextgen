package com.eventty.eventtynextgen.certification.core.userdetails;

public interface UserDetailsService {

    UserDetails loadUserDetailsByLoginId(String loginId);
}
