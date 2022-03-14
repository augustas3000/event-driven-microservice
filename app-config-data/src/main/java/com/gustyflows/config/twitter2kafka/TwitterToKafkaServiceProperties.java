package com.gustyflows.config.twitter2kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("twitter-to-kafka-service")
public class TwitterToKafkaServiceProperties {
    private List<String> twitterKeywords;
    private Boolean enableMockTweets;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;
    private Integer mockSleepMs;
}
