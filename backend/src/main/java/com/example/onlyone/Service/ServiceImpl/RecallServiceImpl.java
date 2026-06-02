package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.Mapper.UserActionMapper;
import com.example.onlyone.Properties.RecommendProperties;
import com.example.onlyone.Service.RecallService;
import com.example.onlyone.Utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecallServiceImpl implements RecallService {


    @Resource
    private UserActionMapper userActionMapper;
    @Resource
    private RecommendProperties props;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //协同过滤路
    @Override
    public Map<Long, Double> collaborativeRecall(Long userId) {

        Map<Long, Double> scores = new HashMap<>();

        if (userId == null) {
            //用户未登录，不做设置，返回空map
            return scores;
        }

        //获取用户一周内互动过的笔记
        LocalDateTime since = LocalDateTime.now().minusDays(props.getOnlineHistoryDays());
        List<Long> historyNotes = userActionMapper.selectRecentLikedNotes(userId,since);

        if (historyNotes.isEmpty()) {
            //无历史行为，跳过
            return  scores;
        }

        //遍历历史互动笔记id集合
        for (Long noteId : historyNotes) {
            //把整个map取出来
            Map<Object,Object> simMap = stringRedisTemplate.opsForHash().entries(RedisKeyUtils.simKey(noteId));
            //遍历map的键值对，用于判断相似笔记集合中哪一些和当前用户历史互动笔记id的相似度最高
            for (Map.Entry<Object, Object> entry : simMap.entrySet()) {
                //候选笔记id
                Long simArticleId = Long.valueOf(entry.getKey().toString());
                //笔记相似度
                double sim = Double.parseDouble(entry.getValue().toString());
                //累加得分,核心是相似笔记中和用户历史互动笔记相似度最高的一些笔记
                scores.merge(simArticleId,sim,Double::sum);
            }
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(props.getRecallCollabLimit())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (v1, v2) -> v1, LinkedHashMap::new));
    }


    /**
     * 热门召回：从 Redis 热度榜 ZSet 中获取当前最热笔记
     * @return Map<noteId, 热度分>（按分数降序排列的 LinkedHashMap）
     */
    @Override
    public Map<Long, Double> hotRecall() {
        // 从 Redis ZSet 中倒序取出前 N 个热门笔记（score 从高到低）
        Set<ZSetOperations.TypedTuple<String>> hotSet = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(RedisKeyUtils.hotNotesKey(), 0, props.getRecallHotLimit() - 1);


        Map<Long, Double> scores = new LinkedHashMap<>();
        if (hotSet != null && !hotSet.isEmpty()) {
            //取出对应的文章id和热值
            for (ZSetOperations.TypedTuple<String> tuple : hotSet) {
                Long noteId = Long.valueOf(tuple.getValue());
                Double hotScore = tuple.getScore();
                scores.put(noteId, hotScore);
            }
        }

        return scores;
    }


    //后续进行补充
    @Override
    public Map<Long, Double> tagRecall(Long userId) {

        return Map.of();
    }


}
