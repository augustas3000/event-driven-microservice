package com.gustyflows.elastic.query.service.api;

import com.gustyflows.elastic.query.service.model.ElasticQueryServiceRequest;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {

    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponse>> getAllDocuments() {
        List<ElasticQueryServiceResponse> responses = new ArrayList<>();
        log.info("Elasticsearch returned {} of documents", responses.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponse> getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponse response = ElasticQueryServiceResponse.builder()
                .id(id)
                .build();
        log.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponse>> getDocumentByText(@RequestBody ElasticQueryServiceRequest request) {
        List<ElasticQueryServiceResponse> responses = new ArrayList<>();
        ElasticQueryServiceResponse response = ElasticQueryServiceResponse.builder()
                .text(request.getText())
                .build();
        responses.add(response);
        log.info("Elasticsearch returned {} of documents", responses.size());
        return ResponseEntity.ok(responses);
    }

}
