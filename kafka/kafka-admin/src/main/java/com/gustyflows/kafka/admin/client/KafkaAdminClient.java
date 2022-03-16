package com.gustyflows.kafka.admin.client;

import com.gustyflows.config.twitter2kafka.KafkaConfigProperties;
import com.gustyflows.config.twitter2kafka.RetryConfigProperties;
import com.gustyflows.kafka.admin.exception.KafkaClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaAdminClient {

    private final KafkaConfigProperties kafkaConfigProperties;
    private final RetryConfigProperties retryConfigProperties;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(
            KafkaConfigProperties kafkaConfigProperties,
            RetryConfigProperties retryConfigProperties,
            AdminClient adminClient,
            RetryTemplate retryTemplate,
            WebClient webClient) {
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.retryConfigProperties = retryConfigProperties;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    /*
    as we intend to run kafka, schema registry in a single docker-compose file. If any of the actors are not running, the application can fail
    at startup, hence  we gonna check the status of these as part of app startup
     */

    public void createTopics() {
        CreateTopicsResult createTopicsResult;

        try {
            createTopicsResult = retryTemplate.execute(retryContext -> doCreateTopics(retryContext));
        } catch (Throwable e) {
            throw new KafkaClientException("Reached max number of retries for creating kafka topic(s)!", e);
        }
        checkTopicsCreated();
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxAttempts = retryConfigProperties.getMaxAttempts();
        int multiplier = retryConfigProperties.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigProperties.getSleepTimeMs();

        for (String topic : kafkaConfigProperties.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxAttempts);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxAttempts = retryConfigProperties.getMaxAttempts();
        int multiplier = retryConfigProperties.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigProperties.getSleepTimeMs();

        while (getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxAttempts);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigProperties.getSchemaRegistryUrl())
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topics", e);
        }
    }

    private void checkMaxRetry(int retry, Integer maxAttempts) {
        if (retry > maxAttempts) {
            throw new KafkaClientException("Max retry count reached, when trying to read kafka topic(s)");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }

        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNamesToCreate = kafkaConfigProperties.getTopicNamesToCreate();
        log.info("Creating {} topic(s), attempt {}", topicNamesToCreate.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNamesToCreate.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigProperties.getNumberOfPartitions(),
                kafkaConfigProperties.getReplicationFactor()
        )).collect(Collectors.toList());

        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(retryContext -> doGetTopics(retryContext));
        } catch (Throwable e) {
            throw new KafkaClientException("Reached max number of retries for getting kafka topic(s)!", e);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        log.info("Getting kafka topic {}, attempt {}",
                kafkaConfigProperties.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> log.debug("Topic with name {}", topic.name()));
        }
        return topics;
    }

}
