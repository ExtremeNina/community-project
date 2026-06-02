package com.example.onlyone.Task;

import com.example.onlyone.Mapper.OfflineNotificationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CleanupTask {

    @Resource
    private OfflineNotificationMapper offlineNotificationMapper;

    @Scheduled(cron = "0 0 3 * * SUN")
    public void cleanOldNotifications() {
        int count = offlineNotificationMapper.deleteSentBeforeDays(30);
        log.info("清理已发送离线消息 {} 条（30天前）", count);
    }
}