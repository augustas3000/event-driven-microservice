package com.gustyflows.twitter2kafka.runner.impl;

import com.gustyflows.config.twitter2kafka.Twitter4jProperties;
import com.gustyflows.config.twitter2kafka.TwitterToKafkaServiceProperties;
import com.gustyflows.twitter2kafka.listener.TwitterKafkaStatusListener;
import com.gustyflows.twitter2kafka.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Component
@Slf4j
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

    private final TwitterToKafkaServiceProperties twitterToKafkaServiceProperties;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private TwitterStream twitterStream;
    private Twitter4jProperties twitter4jProperties;

    public TwitterKafkaStreamRunner(TwitterToKafkaServiceProperties twitterToKafkaServiceProperties, TwitterKafkaStatusListener twitterKafkaStatusListener, Twitter4jProperties twitter4jProperties) {
        this.twitterToKafkaServiceProperties = twitterToKafkaServiceProperties;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
        this.twitter4jProperties = twitter4jProperties;
    }

    @Override
    public void start() throws TwitterException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitter4jProperties.getConsumerKey())
                .setOAuthConsumerSecret(twitter4jProperties.getConsumerSecret())
                .setOAuthAccessToken(twitter4jProperties.getAccessToken())
                .setOAuthAccessTokenSecret(twitter4jProperties.getAccessTokenSecret());

        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
        this.twitterStream = twitterStreamFactory.getInstance();
        twitterStream.addListener(twitterKafkaStatusListener);
        addFilter();
    }

    @PreDestroy
    public void shutdown() {
        if (twitterStream != null) {
            log.info("Closing twitter stream");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String[] keywords = twitterToKafkaServiceProperties.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        log.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
    }
}
