package com.gustyflows.kafka.consumer.config;

import com.gustyflows.config.twitter2kafka.KafkaConfigProperties;
import com.gustyflows.config.twitter2kafka.KafkaConsumerProperties;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final KafkaConfigProperties kafkaConfigProperties;

    public KafkaConsumerConfig(KafkaConsumerProperties kafkaConsumerProperties, KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(kafkaConsumerProperties.getSpecificAvroReaderKey(), kafkaConsumerProperties.getSpecificAvroReader());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getValueDeserializer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getConsumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerProperties.getAutoOffsetReset());
        props.put(kafkaConfigProperties.getSchemaRegistryUrlKey(), kafkaConfigProperties.getSchemaRegistryUrl());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerProperties.getSessionTimeoutMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerProperties.getHeartBeatInterval());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerProperties.getMaxPollIntervalMs());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                kafkaConsumerProperties.getMaxPartitionFetchBytesDefault() *
                        kafkaConsumerProperties.getMaxPartitionFetchBytesBoostFactor());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerProperties.getMaxPollRecords());

        return props;
    }

    @Bean
    public ConsumerFactory<K, V> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>> kafkaListenerContainerFactory(
            ConsumerFactory<K, V> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<K, V>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(kafkaConsumerProperties.getBatchListener());
        factory.setConcurrency(kafkaConsumerProperties.getConcurrencyLevel());
        factory.setAutoStartup(kafkaConsumerProperties.getAutoStartup());
        factory.getContainerProperties().setPollTimeout(kafkaConsumerProperties.getPollTimeoutMs());
        return factory;
    }


}

