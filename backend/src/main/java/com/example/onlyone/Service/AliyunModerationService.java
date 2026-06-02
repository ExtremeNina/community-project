package com.example.onlyone.Service;

import com.example.onlyone.DTO.ModerationResult;

public interface AliyunModerationService {

    ModerationResult checkText(String content);
}