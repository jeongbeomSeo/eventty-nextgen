package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.user.entity.enumtype.UserRole;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;

public interface UserService {

    UserSignupResponseView signup(String email, String password, UserRole userRole, String name, String phone, String birth);

    UserUpdateResponseView update(Long userId, String name, String phone, String birth);

    UserDeleteResponseView delete(Long userId);
}
