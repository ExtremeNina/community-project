package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.DTO.ModerationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ModerationDecider {

    public String decideBusinessStatus(ModerationResult result) {
        return switch (result.getSuggestion()) {
            case "pass" -> "1";
            case "block" -> "2";
            case "review" -> "3";
            default -> "3";
        };
    }

    public String decideRecordStatus(ModerationResult result) {
        return switch (result.getSuggestion()) {
            case "pass" -> "auto_approved";
            case "block" -> "auto_rejected";
            case "review" -> "human_review";
            default -> "human_review";
        };
    }
}