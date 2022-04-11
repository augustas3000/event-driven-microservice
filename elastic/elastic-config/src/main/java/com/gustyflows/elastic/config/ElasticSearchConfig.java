package com.gustyflows.elastic.config;

import com.gustyflows.config.twitter2kafka.ElasticConfigProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.gustyflows.elastic.index.client.repository")
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private final ElasticConfigProperties elasticConfigProperties;

    public ElasticSearchConfig(ElasticConfigProperties elasticConfigProperties) {
        this.elasticConfigProperties = elasticConfigProperties;
    }

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticConfigProperties.getConnectionUrl()).build();
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        Objects.requireNonNull(serverUri.getHost()),
                        serverUri.getPort(),
                        serverUri.getScheme()
                )).setRequestConfigCallback(reqConfigBuilder ->
                        reqConfigBuilder
                                .setConnectTimeout(elasticConfigProperties.getConnectTimeoutMs())
                                .setSocketTimeout(elasticConfigProperties.getSocketTimeoutMs()))
        );
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
