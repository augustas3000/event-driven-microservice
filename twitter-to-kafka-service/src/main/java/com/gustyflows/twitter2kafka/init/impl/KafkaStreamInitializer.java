package com.gustyflows.twitter2kafka.init.impl;

import com.gustyflows.config.twitter2kafka.KafkaConfigProperties;
import com.gustyflows.kafka.admin.client.KafkaAdminClient;
import com.gustyflows.twitter2kafka.init.StreamInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaStreamInitializer implements StreamInitializer {

    private final KafkaConfigProperties kafkaConfigProperties;
    private final KafkaAdminClient kafkaAdminClient;

    public KafkaStreamInitializer(KafkaConfigProperties kafkaConfigProperties, KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        log.info("Topics with name {} are ready for operations!", kafkaConfigProperties.getTopicNamesToCreate().toArray());
    }

}
