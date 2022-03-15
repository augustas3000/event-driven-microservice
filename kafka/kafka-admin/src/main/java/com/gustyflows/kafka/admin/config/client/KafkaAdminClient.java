package com.gustyflows.kafka.admin.config.client;

import com.gustyflows.config.twitter2kafka.KafkaConfigProperties;
import com.gustyflows.config.twitter2kafka.RetryConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaAdminClient {

    private final KafkaConfigProperties kafkaConfigProperties;
    private final RetryConfigProperties retryConfigProperties;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;

    public KafkaAdminClient(
            KafkaConfigProperties kafkaConfigProperties,
            RetryConfigProperties retryConfigProperties,
            AdminClient adminClient,
            RetryTemplate retryTemplate) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.retryConfigProperties = retryConfigProperties;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
    }
}
