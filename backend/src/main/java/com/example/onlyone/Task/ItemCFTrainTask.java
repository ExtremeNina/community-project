package com.example.onlyone.Task;


import com.example.onlyone.Mapper.UserActionMapper;
import com.example.onlyone.Properties.RecommendProperties;
import com.example.onlyone.Utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class ItemCFTrainTask {


    @Resource
    private RecommendProperties props;
    @Resource
    private UserActionMapper userActionMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Scheduled(cron = "0 0 2 * * ?")
    public void train() {
        log.info("离线训练开始");
        long start = System.currentTimeMillis();

        //1.获取30天所有用户的交互行为
        LocalDateTime since = LocalDateTime.now().minusDays(props.getHistoryDays());
        //获取互动的用户id和文章id
        List<Map<String,Long>> action = userActionMapper.selectPositiveActions(since);
        Map<Long, Set<Long>> userLikes = new HashMap<>();
        //从map集合中获取文章id和用户id，然后封装到map中
        for(Map<String,Long> row : action) {
            Long userId = row.get("user_id");
            Long articleId = row.get("article_id");
            userLikes.computeIfAbsent(userId, k -> new HashSet<>()).add(articleId);
        }
        log.info("抽取到 {} 个用户的正向行为数据", userLikes.size());

        //计算笔记的流行度
        //map集合为笔记id和出现次数
        Map<Long, Integer> pop = new HashMap<>();
        //从每一个用户点赞过的笔记列表中求和出总数
        for (Set<Long> notes : userLikes.values()) {
            for (Long noteId : notes) {
                pop.merge(noteId, 1, Integer::sum);
            }
        }

        //计算文章共现矩阵
        //第一个map的作用：笔记a和笔记b共现的次数
        Map<Long,Map<Long,Integer>> cooc = new HashMap<>();
        //获取用户正向的笔记列表
        for(Set<Long> notes : userLikes.values()) {
            List<Long> noteList = new ArrayList<>(notes);
            for (int i = 0; i < noteList.size(); i++) {
                Long noteA = noteList.get(i);
                for (int j = i + 1; j < noteList.size(); j++) {
                    Long noteB = noteList.get(j);
                    // 因为是双向关系，noteA 和 noteB 的共现次数都要加 1
                    addCooc(cooc, noteA, noteB);
                    addCooc(cooc, noteB, noteA);
                }
            }
        }

        //构建余弦相似度
        int k = props.getSimilarityTopK();
        int processedCount = 0;
        //遍历共现矩阵，获取笔记和笔记之间的关系
        for(Map.Entry<Long, Map<Long,Integer>> entry : cooc.entrySet()) {
            //当前笔记id
            Long articleId = entry.getKey();
            //和另外一本笔记的id以及共现次数
            Map<Long, Integer> neighbors = entry.getValue();
            //笔记的流行度
            double popI = pop.getOrDefault(articleId, 1);

            // 按相似度值升序排列 → 堆顶永远是最小值
            PriorityQueue<Map.Entry<Long, Double>> topKHeap = new PriorityQueue<>(
                    Comparator.comparingDouble(Map.Entry::getValue)   // 按相似度值升序排列 → 堆顶永远是最小值
            );

            for (Map.Entry<Long, Integer> neighbor : neighbors.entrySet()) {
                Long otherNoteId = neighbor.getKey();          // 笔记 j
                int coocCount = neighbor.getValue();           // 同时喜欢 i 和 j 的用户数
                double popJ = pop.getOrDefault(otherNoteId, 1); // 笔记 j 的流行度

                // 余弦相似度公式：共现次数 / sqrt(popI * popJ)
                double sim = coocCount / Math.sqrt(popI * popJ);

                topKHeap.offer(new AbstractMap.SimpleEntry<>(otherNoteId, sim));  // 入堆

                if (topKHeap.size() > k) {
                    topKHeap.poll();   // 堆超过 K 个就踢掉堆顶（当前最不相似的）
                }
            }

            //将k个相似笔记写入redis
            String redisKey = RedisKeyUtils.simKey(articleId);
            //清楚旧数据
            stringRedisTemplate.delete(redisKey);
            Map<String, String> simMap = new HashMap<>();
            while (!topKHeap.isEmpty()) {
                Map.Entry<Long, Double> e = topKHeap.poll();
                simMap.put(e.getKey().toString(), e.getValue().toString());
            }
            if (!simMap.isEmpty()) {
                stringRedisTemplate.opsForHash().putAll(redisKey, simMap);  // 批量写入 Redis
            }
            processedCount++;
        }

        long elapsed = System.currentTimeMillis() - start;
        log.info("离线训练完成，共处理 {} 个物品，耗时 {} 毫秒", processedCount, elapsed);


    }


    /**
     * 向共现矩阵中添加一对笔记的共现关系
     * @param cooc 共现矩阵
     * @param a 笔记A
     * @param b 笔记B
     */
    private void addCooc(Map<Long, Map<Long, Integer>> cooc, Long a, Long b) {
        // computeIfAbsent: 如果笔记 a 没有共现行，先创建一个 HashMap
        // merge: 将 b 的共现次数 +1，如果之前不存在则从 0 开始
        cooc.computeIfAbsent(a, k -> new HashMap<>()).merge(b, 1, Integer::sum);
    }


//    @EventListener(ApplicationReadyEvent.class)
//    public void onApplicationReady() {
//        log.info("应用启动，立即执行一次离线训练");
//        train();
//    }

}
