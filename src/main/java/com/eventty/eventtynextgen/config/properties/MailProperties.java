package com.eventty.eventtynextgen.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String defaultEncoding;
    private int timeout;
    private boolean starttlsEnable;
    private boolean auth;
    private boolean debug;
}
