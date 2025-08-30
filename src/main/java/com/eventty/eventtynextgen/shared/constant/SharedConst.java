package com.eventty.eventtynextgen.shared.constant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SharedConst {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final PathMatcher PATH_MATCHER = new AntPathMatcher();
}
