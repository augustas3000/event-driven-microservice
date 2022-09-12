package com.gustyflows.elastic.query.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.hateoas.FeignHalAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.gustyflows", exclude = {FeignHalAutoConfiguration.class})
public class ElasticQueryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryServiceApplication.class, args);
    }
}
