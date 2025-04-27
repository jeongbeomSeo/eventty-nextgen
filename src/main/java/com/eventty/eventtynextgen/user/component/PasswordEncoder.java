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

    /**
     * 평문(rawPassword)와, DB 에 저장된 해시(encodedPassword)를 비교합니다.
     *
     * 참고: BCrypt.checkpw 는 encodedPassword 내부의 salt 정보를 사용해서 검증합니다.
     *
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 해싱 처리된 비밀번호
     * @return 비교 결과
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}
