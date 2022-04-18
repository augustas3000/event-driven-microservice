package com.gustyflows.elastic.index.client.service.impl;

import com.gustyflows.config.ElasticConfigProperties;
import com.gustyflows.elastic.index.client.service.ElasticIndexClient;
import com.gustyflows.elastic.index.client.util.ElasticIndexUtil;
import com.gustyflows.elastic.model.index.impl.TwitterIndexModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "false")
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final ElasticConfigProperties elasticConfigProperties;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    public TwitterElasticIndexClient(
            ElasticConfigProperties elasticConfigProperties,
            ElasticsearchOperations elasticsearchOperations,
            ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil
    ) {
        this.elasticConfigProperties = elasticConfigProperties;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticIndexUtil = elasticIndexUtil;
    }


    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);

        List<IndexedObjectInformation> indexedObjectInformations = elasticsearchOperations.bulkIndex(
                indexQueries,
                IndexCoordinates.of(elasticConfigProperties.getIndexName())
        );

        List<String> documentIds =
                indexedObjectInformations.stream().map(IndexedObjectInformation::getId).collect(Collectors.toList());

        log.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(), documentIds);
        return documentIds;
    }
}
