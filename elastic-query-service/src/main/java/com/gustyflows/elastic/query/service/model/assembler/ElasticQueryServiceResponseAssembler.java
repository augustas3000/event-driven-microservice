package com.gustyflows.elastic.query.service.model.assembler;

import com.gustyflows.elastic.model.index.impl.TwitterIndexModel;
import com.gustyflows.elastic.query.service.api.ElasticDocumentController;
import com.gustyflows.elastic.query.service.model.ElasticQueryServiceResponse;
import com.gustyflows.elastic.query.service.transformer.ElasticToResponseModelTransformer;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseAssembler extends RepresentationModelAssemblerSupport<TwitterIndexModel, ElasticQueryServiceResponse> {

    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public ElasticQueryServiceResponseAssembler(ElasticToResponseModelTransformer elasticToResponseModelTransformer) {
        super(ElasticDocumentController.class, ElasticQueryServiceResponse.class);
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
    }

    @Override
    public ElasticQueryServiceResponse toModel(TwitterIndexModel twitterIndexModel) {
        ElasticQueryServiceResponse responseModel = elasticToResponseModelTransformer.getResponseModel(twitterIndexModel);

        responseModel.add(
                linkTo(methodOn(ElasticDocumentController.class)
                        .getDocumentById(twitterIndexModel.getId())).withSelfRel()
        );

        responseModel.add(
                linkTo(ElasticDocumentController.class)
                        .withRel("documents")
        );
        return responseModel;
    }

    public List<ElasticQueryServiceResponse> toModels(List<TwitterIndexModel> twitterIndexModels) {
        return twitterIndexModels.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
