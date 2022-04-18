package com.gustyflows.common.config;

import com.gustyflows.config.RetryConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    private final RetryConfigProperties retryConfigProperties;

    public RetryConfig(RetryConfigProperties retryConfigProperties) {
        this.retryConfigProperties = retryConfigProperties;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(retryConfigProperties.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(retryConfigProperties.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(retryConfigProperties.getMultiplier());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryConfigProperties.getMaxAttempts());

        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

}
