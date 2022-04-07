package com.gustyflows.config.twitter2kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-config")
public class ElasticConfigProperties {
    private String indexName;
    private String connectionUrl;
    private Integer connectTimeoutMs;
    private Integer socketTimeoutMs;
}
