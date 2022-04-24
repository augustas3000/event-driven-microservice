package com.gustyflows.elastic.query.service.business;

import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponse getDocumentById(String id);

    List<ElasticQueryServiceResponse> getDocumentByText(String text);

    List<ElasticQueryServiceResponse> getAllDocuments();

}
