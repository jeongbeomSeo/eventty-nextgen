package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.response.UserActivateDeletedUserResponseView;
import com.eventty.eventtynextgen.user.response.UserChangePasswordResponseView;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserFindEmailResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;

public interface UserService {

    UserSignupResponseView signup(String email, String password, UserRoleType userRole, String name, String phone, String birth);

    UserUpdateResponseView update(Long userId, String name, String phone, String birth);

    UserDeleteResponseView delete(Long userId);

    UserActivateDeletedUserResponseView activateToDeletedUser(Long userId);

    UserFindEmailResponseView findEmailByPersonalInfo(String name, String phone);

    UserChangePasswordResponseView changePassword(Long userId, String currentPassword, String updatedPassword);
}
