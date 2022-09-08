package com.gustyflows.elastic.query.service.api;

import com.gustyflows.elastic.query.service.business.ElasticQueryService;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceRequest;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService queryService) {
        this.elasticQueryService = queryService;
    }

    @Operation(summary = "Get all elastic documents.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponse.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponse>> getAllDocuments() {
        List<ElasticQueryServiceResponse> response = elasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponse.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponse>
    getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponse elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseV2.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseV2>
    getDocumentByIdV2(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponse elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        ElasticQueryServiceResponseV2 responseModelV2 = getV2Model(elasticQueryServiceResponseModel);
        log.debug("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(responseModelV2);
    }

    @Operation(summary = "Get elastic document by text.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponse.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/get-document-by-text")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponse>>
    getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequest ElasticQueryServiceRequest) {
        List<ElasticQueryServiceResponse> response =
                elasticQueryService.getDocumentByText(ElasticQueryServiceRequest.getText());
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseV2 getV2Model(ElasticQueryServiceResponse responseModel) {
        ElasticQueryServiceResponseV2 responseModelV2 = ElasticQueryServiceResponseV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;

    }
}
