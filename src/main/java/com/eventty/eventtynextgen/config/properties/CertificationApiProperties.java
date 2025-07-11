package com.eventty.eventtynextgen.config.properties;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "certification")
public class CertificationApiProperties {

    private Map<String, Set<String>> apiPermissionMap;
}

