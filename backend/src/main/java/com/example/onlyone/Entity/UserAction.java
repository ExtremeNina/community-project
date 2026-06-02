package com.example.onlyone.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAction {

    private Long id;
    private Long userId;
    private Long articleId;
    private String actionType;
    private LocalDateTime createTime;
    private Integer weight;
}
