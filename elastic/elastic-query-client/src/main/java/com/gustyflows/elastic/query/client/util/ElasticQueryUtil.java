package com.gustyflows.elastic.query.client.util;

import com.gustyflows.elastic.model.index.IndexModel;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ElasticQueryUtil<T extends IndexModel> {

    public Query getSearchQueryById(String id) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withIds(Collections.singleton(id)).build();
        return nativeSearchQuery;
    }

    public Query getSearchQueryByFieldText(String field, String text) {
        return new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder()
                        .must(QueryBuilders.matchQuery(field, text)))
                .build();
    }

    public Query getSearchQueryForAll() {
        return new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder()
                        .must(QueryBuilders.matchAllQuery()))
                .build();
    }
}
