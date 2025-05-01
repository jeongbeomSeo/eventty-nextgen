package com.eventty.eventtynextgen.user.response;

import java.util.List;

public record UserFindEmailResponseView(List<UserEmailInfo> userEmailInfos) {
    public record UserEmailInfo(Long userId, String email) {
    }
}