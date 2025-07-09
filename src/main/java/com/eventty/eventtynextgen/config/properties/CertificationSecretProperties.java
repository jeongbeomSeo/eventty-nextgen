package com.eventty.eventtynextgen.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "certification-secret")
public class CertificationSecretProperties {

    private Map<String, String> certificationSecrets;
}
