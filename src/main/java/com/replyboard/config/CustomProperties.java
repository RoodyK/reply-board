package com.replyboard.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Getter
@ConfigurationProperties(prefix = "api")
public class CustomProperties {

    private final String loginUrl;
    private final String logoutUrl;

    @ConstructorBinding
    public CustomProperties(String loginUrl, String logoutUrl) {
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
    }
}
