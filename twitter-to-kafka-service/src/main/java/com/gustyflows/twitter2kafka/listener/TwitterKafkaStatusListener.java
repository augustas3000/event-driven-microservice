package com.gustyflows.twitter2kafka.listener;

import com.gustyflows.config.KafkaConfigProperties;
import com.gustyflows.kafka.avro.model.TwitterAvroModel;
import com.gustyflows.kafka.producer.service.KafkaProducer;
import com.gustyflows.twitter2kafka.transformer.TwitterStatusToAvroTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final KafkaConfigProperties kafkaConfigProperties;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    public TwitterKafkaStatusListener(KafkaConfigProperties kafkaConfigProperties, KafkaProducer<Long, TwitterAvroModel> kafkaProducer, TwitterStatusToAvroTransformer twitterStatusToAvroTransformer) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.kafkaProducer = kafkaProducer;
        this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
    }


    @Override
    public void onStatus(Status status) {
        log.info("Received status text {}. Sending to kafka topic {}", status.getText(), kafkaConfigProperties.getTopicName());
        TwitterAvroModel twitterAvroModelFromStatus = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigProperties.getTopicName(), twitterAvroModelFromStatus.getUserId(), twitterAvroModelFromStatus);
    }
}
