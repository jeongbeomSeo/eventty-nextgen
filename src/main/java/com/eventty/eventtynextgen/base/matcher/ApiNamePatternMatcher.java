package com.eventty.eventtynextgen.base.matcher;

import com.eventty.eventtynextgen.base.enums.ApiName;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiNamePatternMatcher {

    public static Optional<ApiName> getApiName(String url) {
        for (ApiName apiName : ApiName.values()) {
            String[] patterns = apiName.getPattern().split(",");

            for (String pattern : patterns) {
                if (url.contains(pattern)) {
                    return Optional.of(apiName);
                }
            }
        }
        return Optional.empty();
    }
}
