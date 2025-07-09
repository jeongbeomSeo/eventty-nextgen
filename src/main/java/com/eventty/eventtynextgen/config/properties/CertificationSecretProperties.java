package com.eventty.eventtynextgen.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
//@Setter // TODO: 확인
@Component
@ConfigurationProperties(prefix = "certifcation-secert")
public class CertificationSecretProperties {

    private Map<String, String> certificationSecrets;
}
