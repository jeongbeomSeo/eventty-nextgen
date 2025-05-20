package com.eventty.eventtynextgen.base.matcher;

import com.eventty.eventtynextgen.base.enums.ApiNameType;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

@UtilityClass
public class ApiNamePatternMatcher {

    private final static AntPathMatcher matcher = new AntPathMatcher();

    public static Optional<ApiNameType> getApiNameByUrlAndMethod(String url, String method) {
        for (ApiNameType api : ApiNameType.values()) {
            if ((api.getMethod().matches(method) || api.getMethod().equals("any"))
                && matcher.match(api.getPattern(), url)) {
                return Optional.of(api);
            }
        }

        return Optional.empty();
    }
}
