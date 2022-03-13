package com.gustyflows.twitter2kafka;

import com.gustyflows.twitter2kafka.config.TwitterToKafkaServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
    private final TwitterToKafkaServiceProperties twitterToKafkaServiceProperties;

    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceProperties twitterToKafkaServiceProperties) {
        this.twitterToKafkaServiceProperties = twitterToKafkaServiceProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        log.info(Arrays.toString(twitterToKafkaServiceProperties.getTwitterKeywords().toArray(new String[]{})));
    }
}
