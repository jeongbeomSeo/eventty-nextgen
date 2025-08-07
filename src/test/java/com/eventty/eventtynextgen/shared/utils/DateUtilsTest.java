package com.eventty.eventtynextgen.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Disabled("Date Utility Class 단위 테스트")
class DateUtilsTest {

    @Nested
    @DisplayName("Date 객체를 LocalDateTime 객체로 변환 테스트")
    class ConvertFormatToLocalDateTime {
        @Test
        @DisplayName("Date 객체를 LocalDateTime 객체로 바꾼다.")
        void Date_객체를_Local_Date_Time_객체로_바꾼다() {
            // given
            Date date = new Date();

            // when
            LocalDateTime localDateTime = DateUtils.convertFormatToLocalDateTime(date);

            // then
            assertThat(localDateTime).isNotNull();
            Instant dateInstant = date.toInstant();
            Instant ldtInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            assertThat(dateInstant).isEqualTo(ldtInstant);
        }
    }
}