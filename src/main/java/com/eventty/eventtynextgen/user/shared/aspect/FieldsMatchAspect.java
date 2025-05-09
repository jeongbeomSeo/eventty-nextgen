package com.eventty.eventtynextgen.user.shared.aspect;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.user.shared.annotation.FieldsMatch;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FieldsMatchAspect {

    @Before("@annotation(fieldsMatch)")
    public void checkPasswordMatch(JoinPoint jp, FieldsMatch fieldsMatch) {
        String baseName = fieldsMatch.base();
        String matchName = fieldsMatch.match();

        for (Object arg : jp.getArgs()) {
            if (arg == null) {
                continue;
            }

            Class<?> cls = arg.getClass();
            try {
                Field baseField = cls.getDeclaredField(baseName);
                Field matchField = cls.getDeclaredField(matchName);

                baseField.setAccessible(true);
                matchField.setAccessible(true);

                Object baseValue = baseField.get(arg);
                Object matchValue = matchField.get(arg);

                if (!Objects.equals(baseValue, matchValue)) {
                    throw CustomException.badRequest(CommonErrorType.INVALID_INPUT_DATA, Map.of("message", fieldsMatch.message()));
                }

                return;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        // 만약 어떤 파라미터에서도 두 필드를 찾지 못했다면
        log.error("FieldsMatch: 객체에 필드 '" + baseName + "' 또는 '" + matchName + "' 이(가) 없습니다.");
        throw new IllegalStateException(
            "FieldsMatch: 객체에 필드 '" + baseName + "' 또는 '" + matchName + "' 이(가) 없습니다.");
    }
}
