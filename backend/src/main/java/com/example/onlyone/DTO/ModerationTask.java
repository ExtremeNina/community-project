package com.example.onlyone.DTO;

import lombok.Data;

@Data
public class ModerationTask {

    private String targetType;
    private Long targetId;
    private String content;
    private Long userId;
}