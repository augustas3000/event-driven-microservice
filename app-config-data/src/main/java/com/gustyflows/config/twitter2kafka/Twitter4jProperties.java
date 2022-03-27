package com.gustyflows.config.twitter2kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter4j.oauth")
public class Twitter4jProperties {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
}
