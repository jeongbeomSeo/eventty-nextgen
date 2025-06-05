package com.eventty.eventtynextgen.auth.authcode;

import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeExistsEmailResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeSendCodeResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeValidateCodeResponseView;

public interface AuthCodeService {

    AuthCodeExistsEmailResponseView exists(String authTarget);

    AuthCodeSendCodeResponseView sendCode(String authTarget);

    AuthCodeValidateCodeResponseView validateCode(String email, String code);
}
