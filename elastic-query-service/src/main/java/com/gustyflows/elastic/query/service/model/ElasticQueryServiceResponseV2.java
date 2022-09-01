package com.gustyflows.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseV2 extends RepresentationModel<ElasticQueryServiceResponseV2> {
    private Long id;
    private Long userId;
    private String text;
    private String text2;
}
