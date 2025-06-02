package com.eventty.eventtynextgen.shared.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    public static LocalDateTime convertFormatToLocalDateTime(Date date) {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
