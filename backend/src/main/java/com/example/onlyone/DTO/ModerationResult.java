package com.example.onlyone.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ModerationResult {

    private String suggestion;
    private BigDecimal confidence;
    private List<String> labels;
    private String reason;
}