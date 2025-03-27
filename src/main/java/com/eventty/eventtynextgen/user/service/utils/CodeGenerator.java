package com.eventty.eventtynextgen.user.service.utils;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    private final Random random = new Random();

    /**
     * 대문자로 이루어진 인증 코드를 생성합니다.
     * @param len 인증 코드 길이
     * @return len 길이를 가지는 무작위 대문자 문자열
     */
    public String generateVerificationCode(int len) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int offset = random.nextInt('Z' - 'A' + 1);
            sb.append((char) ('A' + offset));
        }

        return sb.toString();
    }
}
