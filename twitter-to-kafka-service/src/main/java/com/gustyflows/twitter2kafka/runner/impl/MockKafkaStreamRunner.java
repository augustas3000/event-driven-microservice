package com.gustyflows.twitter2kafka.runner.impl;

import com.gustyflows.config.twitter2kafka.TwitterToKafkaServiceProperties;
import com.gustyflows.twitter2kafka.listener.TwitterKafkaStatusListener;
import com.gustyflows.twitter2kafka.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private final TwitterToKafkaServiceProperties twitterToKafkaServiceProperties;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[]{
            "Fusce", "egestas", "efficitur", "lobortis", "Vestibulum", "mi", "turpis", "dignissim", "nec", "velit"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\" : \"{0}\"," +
            "\"id\" : \"{1}\"," +
            "\"text\" : \"{2}\"," +
            "\"user\" : {\"id\" : \"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public MockKafkaStreamRunner(TwitterToKafkaServiceProperties twitterToKafkaServiceProperties, TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.twitterToKafkaServiceProperties = twitterToKafkaServiceProperties;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        String[] keywords = twitterToKafkaServiceProperties.getTwitterKeywords().toArray(new String[0]);
        Integer mockMinTweetLength = twitterToKafkaServiceProperties.getMockMinTweetLength();
        Integer mockMaxTweetLength = twitterToKafkaServiceProperties.getMockMaxTweetLength();
        Integer mockSleepMs = twitterToKafkaServiceProperties.getMockSleepMs();
        log.info("Starting to mock filtering twitter streams for keywords {}", Arrays.toString(keywords));
        simulateTwitterStream(keywords, mockMinTweetLength, mockMaxTweetLength, mockSleepMs);
    }

    private void simulateTwitterStream(String[] keywords, Integer mockMinTweetLength, Integer mockMaxTweetLength, Integer mockSleepMs) {
        //we want this to run in a separate thread instead of blocking the main thread
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, mockMinTweetLength, mockMaxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    //in Mock implementation, instead of library calling onStatus, we call it programatically on each iteration.
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(mockSleepMs);
                }
            } catch (TwitterException e) {
                log.error("Error creating twitter status!", e);
            }
        });
    }

    private void sleep(Integer mockSleepMs) {
        try {
            Thread.sleep(mockSleepMs);
        } catch (Exception e) {
            throw new com.gustyflows.twitter2kafka.Exception.TwitterToKafkaServiceException("Error while sleeping the thread before instantiating new twitter Status object", e);
        }
    }

    private String getFormattedTweet(String[] keywords, Integer mockMinTweetLength, Integer mockMaxTweetLength) {
        String[] params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, mockMinTweetLength, mockMaxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };

        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, Integer mockMinTweetLength, Integer mockMaxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(mockMaxTweetLength - mockMinTweetLength + 1) + mockMinTweetLength;
        return constructRandomTweet(keywords, tweet, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }
}
