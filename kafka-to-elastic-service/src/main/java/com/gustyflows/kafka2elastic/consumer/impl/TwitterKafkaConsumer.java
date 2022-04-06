package com.gustyflows.kafka2elastic.consumer.impl;

import com.gustyflows.config.twitter2kafka.KafkaConfigProperties;
import com.gustyflows.config.twitter2kafka.KafkaConsumerProperties;
import com.gustyflows.kafka.admin.client.KafkaAdminClient;
import com.gustyflows.kafka.avro.model.TwitterAvroModel;
import com.gustyflows.kafka2elastic.consumer.KafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigProperties kafkaConfigProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;
//    private final AvroToElasticModelTransformer avroToElasticModelTransformer;
//    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;

    public TwitterKafkaConsumer(
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            KafkaAdminClient kafkaAdminClient,
            KafkaConfigProperties kafkaConfigProperties,
            KafkaConsumerProperties kafkaConsumerProperties
//            AvroToElasticModelTransformer avroToElasticModelTransformer
    ) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.kafkaConsumerProperties = kafkaConsumerProperties;
//        this.avroToElasticModelTransformer = avroToElasticModelTransformer;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name {} is ready for operations!", kafkaConfigProperties.getTopicNamesToCreate().toArray());
        Objects.requireNonNull(kafkaListenerEndpointRegistry
                .getListenerContainer(kafkaConsumerProperties.getConsumerGroupId())).start();
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(
            @Payload List<TwitterAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of messages received with keys {}, partitions {} and  offsets {}, " +
                        "sending messages to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
//todo implement sendting to lastic
        
//        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
//        List<String> documentIds = elasticIndexClient.save(twitterIndexModels);
//        log.info("Documents saved to elasticsearch with ids {}", documentIds.toArray());
    }
}
