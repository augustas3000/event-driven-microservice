package com.gustyflows.twitter2kafka;

import com.gustyflows.twitter2kafka.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(
        scanBasePackages = {
                "com.gustyflows.twitter2kafka",
                "com.gustyflows.config.twitter2kafka"
        }
)
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
    private final StreamRunner streamRunner;

    public TwitterToKafkaServiceApplication(StreamRunner streamRunner) {
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        streamRunner.start();
    }
}
