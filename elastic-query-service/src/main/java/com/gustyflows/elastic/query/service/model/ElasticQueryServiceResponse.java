package com.gustyflows.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponse {
    private String id;
    private Long userId;
    private String text;
    private LocalDateTime createdAt;
}
