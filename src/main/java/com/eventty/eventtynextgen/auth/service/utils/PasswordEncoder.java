package com.eventty.eventtynextgen.auth.service.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    /**
     * 비밀번호를 해싱합니다.
     * @param plainPassword 사용자가 입력한 평문 비밀번호
     * @return 해싱된 비밀번호 (salt 포함)
     */
    public String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt();

        return BCrypt.hashpw(plainPassword, salt);
    }
}
