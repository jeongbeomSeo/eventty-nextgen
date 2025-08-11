package com.eventty.eventtynextgen.shared.utils;

import java.util.concurrent.ThreadLocalRandom;

public class CodeGeneratorUtils {

    /**
     * 숫자와 문자로 이루어진 랜덤 코드를 생성합니다.
     * @param len 랜덤 코드 길이
     * @return len 길이를 가지는 무작위 대문자 문자열
     */
    public static String generateRandomCode(int len) {
        // 대문자, 소문자, 숫자를 모두 포함한 문자 집합
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "0123456789";
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private CodeGeneratorUtils() {}
}