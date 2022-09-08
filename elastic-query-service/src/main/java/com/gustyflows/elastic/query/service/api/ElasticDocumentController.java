package com.gustyflows.elastic.query.service.api;

import com.gustyflows.elastic.query.service.business.ElasticQueryService;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceRequest;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponseV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponse>> getAllDocuments() {
        List<ElasticQueryServiceResponse> responses = elasticQueryService.getAllDocuments();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponse> getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponse response = elasticQueryService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ResponseEntity<ElasticQueryServiceResponseV2> getDocumentByIdV2(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponse response = elasticQueryService.getDocumentById(id);
        return ResponseEntity.ok(getV2Model(response));
    }

    private ElasticQueryServiceResponseV2 getV2Model(ElasticQueryServiceResponse response) {
        return ElasticQueryServiceResponseV2.builder()
                .id(Long.parseLong(response.getId()))
                .text(response.getText())
                .text2("version 2 text")
                .userId(response.getUserId())
                .build()
                .add(response.getLinks());
    }

    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponse>> getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequest request) {
        List<ElasticQueryServiceResponse> responses = elasticQueryService.getDocumentByText(request.getText());
        return ResponseEntity.ok(responses);
    }

}
