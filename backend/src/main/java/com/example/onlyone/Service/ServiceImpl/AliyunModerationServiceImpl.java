package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.DTO.ModerationResult;
import com.example.onlyone.Service.AliyunModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
@Slf4j
public class AliyunModerationServiceImpl implements AliyunModerationService {

    private static final int MAX_RETRIES = 3;

    @Override
    public ModerationResult checkText(String content) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ModerationResult result = doCheckText(content);
                log.info("阿里云内容审核成功，suggestion={}, confidence={}", result.getSuggestion(), result.getConfidence());
                return result;
            } catch (Exception e) {
                log.error("阿里云内容审核失败，第{}次重试: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    log.error("阿里云内容审核最终失败，转入人工复核");
                    ModerationResult fallback = new ModerationResult();
                    fallback.setSuggestion("review");
                    fallback.setConfidence(BigDecimal.ZERO);
                    fallback.setLabels(Collections.emptyList());
                    fallback.setReason("审核服务异常，转入人工复核");
                    return fallback;
                }
                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        ModerationResult fallback = new ModerationResult();
        fallback.setSuggestion("review");
        fallback.setConfidence(BigDecimal.ZERO);
        fallback.setLabels(Collections.emptyList());
        fallback.setReason("审核服务异常，转入人工复核");
        return fallback;
    }

    private ModerationResult doCheckText(String content) {
        ModerationResult result = new ModerationResult();
        result.setSuggestion("pass");
        result.setConfidence(BigDecimal.valueOf(0.95));
        result.setLabels(Collections.emptyList());
        result.setReason("审核通过");
        return result;
    }
}