package com.eventty.eventtynextgen.shared.utils;

import com.eventty.eventtynextgen.base.exception.CustomException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LoggerUtils {

    public static void error(CustomException customException) {
        log.error("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            customException.getDetail());
    }

    public static void debug(CustomException customException) {
        log.debug("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            customException.getDetail());
    }

    public static void info(CustomException customException) {
        info(customException, customException.getDetail() == null ? "" : customException.getDetail().toString());
    }

    public static void info(CustomException customException, String detail) {
        log.info("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            detail);
    }

    public static void warn(CustomException customException) {
        log.warn("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            customException.getDetail());
    }
}
