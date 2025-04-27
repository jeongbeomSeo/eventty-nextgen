package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.request.UserFindAccountRequestCommand;

public class FindAccountRequestFixture {
    public static UserFindAccountRequestCommand createFindAccountRequest(String name, String phone) {
        return new UserFindAccountRequestCommand(name, phone);
    }

}
