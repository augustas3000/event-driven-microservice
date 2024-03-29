package com.gustyflows.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponse extends RepresentationModel<ElasticQueryServiceResponse> {
    private String id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;
}
