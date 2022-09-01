package com.gustyflows.elastic.query.service.business.impl;

import com.gustyflows.elastic.model.index.impl.TwitterIndexModel;
import com.gustyflows.elastic.query.client.service.ElasticQueryClient;
import com.gustyflows.elastic.query.service.business.ElasticQueryService;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;
import com.gustyflows.elastic.query.service.model.assembler.ElasticQueryServiceResponseAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseAssembler elasticQueryServiceResponseAssembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    public TwitterElasticQueryService(
            ElasticQueryServiceResponseAssembler elasticQueryServiceResponseAssembler,
            ElasticQueryClient<TwitterIndexModel> elasticQueryClient
    ) {
        this.elasticQueryServiceResponseAssembler = elasticQueryServiceResponseAssembler;
        this.elasticQueryClient = elasticQueryClient;
    }

    @Override
    public ElasticQueryServiceResponse getDocumentById(String id) {
        log.info("Querying  elasticsearch by id {}", id);
        TwitterIndexModel response = elasticQueryClient.getIndexModelById(id);
        return elasticQueryServiceResponseAssembler.toModel(response);
    }

    @Override
    public List<ElasticQueryServiceResponse> getDocumentByText(String text) {
        log.info("Querying elasticsearch by text {}", text);
        List<TwitterIndexModel> responses = elasticQueryClient.getIndexModelByText(text);
        return elasticQueryServiceResponseAssembler.toModels(responses);
    }

    @Override
    public List<ElasticQueryServiceResponse> getAllDocuments() {
        log.info("Querying all documents in elasticsearch");
        List<TwitterIndexModel> responses = elasticQueryClient.getAllIndexModels();
        return elasticQueryServiceResponseAssembler.toModels(responses);
    }
}
