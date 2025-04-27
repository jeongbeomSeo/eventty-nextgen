package com.eventty.eventtynextgen.user.response;

import java.util.List;

public record UserFindAccountResponseView(List<Account> accounts) {
    record Account(Long userId, String email, boolean isDeleted) {

    }
}