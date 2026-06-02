package com.example.onlyone.Task;


import com.example.onlyone.Mapper.UserActionMapper;
import com.example.onlyone.Utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HotNotesUpdateTask {

    @Resource
    private UserActionMapper userActionMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    private static final int HOT_LIMIT = 500;


    @Scheduled(cron = "0 0 * * * ?")
    public void hotNotesUpdate() {
        log.info("计算热门笔记");
        //计算七天时间
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        //获取七天内最热门的笔记集合
        List<Map<String,Object>> rows = userActionMapper.selectHotNotes(since,HOT_LIMIT);

        // 清除旧的 Redis 热门榜
        String hotKey = RedisKeyUtils.hotNotesKey();
        stringRedisTemplate.delete(hotKey);

        for (Map<String,Object> row : rows) {
            //获取文章id
            Long articleId = (Long) row.get("articleId");
            //获取文章对应的热度
            Double hot = ((Number) row.get("hot")).doubleValue();
            //写入redis中
            stringRedisTemplate.opsForZSet().add(hotKey, articleId.toString(), hot);
        }


    }


//    @EventListener(ApplicationReadyEvent.class)
//    public void onApplicationReady() {
//        log.info("应用启动，立即执行一次热门笔记计算");
//        hotNotesUpdate();
//    }
}
