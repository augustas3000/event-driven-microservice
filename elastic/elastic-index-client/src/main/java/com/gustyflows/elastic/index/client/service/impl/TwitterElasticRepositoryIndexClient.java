package com.gustyflows.elastic.index.client.service.impl;

import com.gustyflows.elastic.index.client.repository.TwitterElasticSearchIndexRepository;
import com.gustyflows.elastic.index.client.service.ElasticIndexClient;
import com.gustyflows.elastic.model.index.impl.TwitterIndexModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true")
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository;

    public TwitterElasticRepositoryIndexClient(TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository) {
        this.twitterElasticSearchIndexRepository = twitterElasticSearchIndexRepository;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> repoResponse =
                (List<TwitterIndexModel>) twitterElasticSearchIndexRepository.saveAll(documents);

        List<String> documentIds = repoResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(), documentIds);
        return documentIds;
    }
}
