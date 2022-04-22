package com.gustyflows.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceRequest {
    private String id;
    private String text;
}
