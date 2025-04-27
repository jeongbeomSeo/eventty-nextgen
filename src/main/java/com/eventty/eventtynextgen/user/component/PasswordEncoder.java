package com.eventty.eventtynextgen.user.component;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    /**
     * 비밀번호를 해싱합니다.
     * @param plainPassword 사용자가 입력한 평문 비밀번호
     * @return 해싱된 비밀번호 (salt 포함)
     */
    public String encode(CharSequence plainPassword) {
        String salt = BCrypt.gensalt();

        return BCrypt.hashpw(plainPassword.toString(), salt);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
