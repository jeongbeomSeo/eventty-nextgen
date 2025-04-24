package com.eventty.eventtynextgen.shared.utils;

import com.eventty.eventtynextgen.shared.utils.sequence.ObjectId;

public class CodeGeneratorUtil {

    /**
     * 대문자로 이루어진 인증 코드를 생성합니다.
     * @param len 인증 코드 길이
     * @return len 길이를 가지는 무작위 대문자 문자열
     */
    public static String generateVerificationCode(int len) {
        return new ObjectId().toHexString().substring(0, len);
    }

    private CodeGeneratorUtil() {}
}