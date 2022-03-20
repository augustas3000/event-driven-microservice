package com.gustyflows.twitter2kafka.transformer;

import com.gustyflows.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransformer {

    public TwitterAvroModel getTwitterAvroModelFromStatus(Status status) {
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setCreatedAt(status.getCreatedAt().getTime())
                .setText(status.getText())
                .setUserId(status.getUser().getId())
                .build();
    }
}
