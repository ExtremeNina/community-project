package com.example.onlyone.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rec")
@Component
@Data
public class RecommendProperties {

    private int similarityTopK = 50;

    private int recallCollabLimit = 200;

    private int recallHotLimit = 100;

    private int recallTagLimit = 80;

    private int candidateCacheTtl = 30;

    private int displayedMaxSize = 200;

    private int historyDays = 30;

    private int onlineHistoryDays = 7;

    private int candidatePoolSize = 200;

    private int refillThreshold = 50;

    private int refillLockTimeout = 30;

}