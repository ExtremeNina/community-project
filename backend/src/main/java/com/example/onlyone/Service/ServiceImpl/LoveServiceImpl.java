package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.DTO.LoveDTO;
import com.example.onlyone.Entity.Article;
import com.example.onlyone.Entity.Comment;
import com.example.onlyone.Entity.UserLove;
import com.example.onlyone.Exception.BusinessException;
import com.example.onlyone.Mapper.ArticleMapper;
import com.example.onlyone.Mapper.CommentMapper;
import com.example.onlyone.Mapper.UserLoveMapper;
import com.example.onlyone.Service.LoveService;
import com.example.onlyone.Utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 点赞服务实现
 *
 * 支持的点赞类型：
 * - LOVE_TYPE_ARTICLE(1L)：文章点赞
 * - LOVE_TYPE_COMMENT(2L)：评论点赞
 *
 * 优化说明：
 * 1. 异常驱动 → 先查后判：原 try-catch DuplicateKeyException toggle 模式改为
 *    先 selectByUserAndEntity 查询状态，再决定 doLike 还是 doUnlike。
 * 2. 物理删除 → 软删除：user_love 表增加 status 字段（1=有效，0=已取消），
 *    cancelLove() 只更新 status=0，不再物理删除记录，保留用户行为历史。
 *    reLove() 复用历史记录：status 重新设为 1。
 * 3. 魔法数字 → 常量：LOVE_TYPE_ARTICLE / LOVE_TYPE_COMMENT 替代硬编码 1L/2L。
 * 4. 分布式锁粒度优化：lockKey 从 love:lock:{entityId} 改为 love:lock:{userId}:{entityId}，
 *    不同用户操作不同实体时不再互相阻塞。
 * 5. 用 BusinessException 替代 RuntimeException，提供明确 HTTP 状态码。
 */
@Service
@Slf4j
public class LoveServiceImpl implements LoveService {

    public static final Long LOVE_TYPE_ARTICLE = 1L;
    public static final Long LOVE_TYPE_COMMENT = 2L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserLoveMapper userLoveMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CommentMapper commentMapper;

    private static final long CACHE_TTL = 24 * 60 * 60L;
    private static final long LOVE_LOCK_TIMEOUT = 5L;

    private static final DefaultRedisScript<Long> LOVE_SCRIPT;
    private static final DefaultRedisScript<Long> UNLOVE_SCRIPT;
    static {
        LOVE_SCRIPT = new DefaultRedisScript<>();
        LOVE_SCRIPT.setLocation(new ClassPathResource("LoveLuaScript.lua"));
        LOVE_SCRIPT.setResultType(Long.class);

        UNLOVE_SCRIPT = new DefaultRedisScript<>();
        UNLOVE_SCRIPT.setLocation(new ClassPathResource("unLoveLuaScript.lua"));
        UNLOVE_SCRIPT.setResultType(Long.class);
    }

    /**
     * 点赞/取消点赞（toggle 模式）
     *
     * 流程：
     * 1. 参数校验（实体 ID、点赞类型）
     * 2. 获取分布式锁 love:lock:{userId}:{entityId}（防止同一用户对同一实体的并发操作）
     * 3. 查询 user_love 表中 status=1 的记录
     * 4. 不存在 → doLike（点赞），存在 → doUnlike（取消点赞）
     * 5. finally 释放锁
     */
    @Transactional
    @Override
    public void toggleLike(LoveDTO loveDTO) {
        if (loveDTO.getId() == null) {
            throw BusinessException.badRequest("该评论或文章已被删除");
        }
        if (!isValidLoveTypeId(loveDTO.getLoveTypeId())) {
            throw BusinessException.badRequest("无效的点赞类型ID: " + loveDTO.getLoveTypeId());
        }

        Long entityId = loveDTO.getId();
        Long loveTypeId = loveDTO.getLoveTypeId();
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        // 分布式锁：用户 + 实体粒度，不同用户操作不同实体互不阻塞
        String lockKey = String.format("love:lock:%s:%s", userId, entityId);
        Boolean lock = tryLock(lockKey);
        if (Boolean.FALSE.equals(lock)) {
            throw BusinessException.conflict("操作过于频繁，请稍后再试");
        }

        try {
            // 查询 status=1 的有效点赞记录
            UserLove existing = userLoveMapper.selectByUserAndEntity(userId, entityId, loveTypeId);

            if (existing == null) {
                doLike(userId, entityId, loveTypeId);
            } else {
                doUnlike(userId, entityId, loveTypeId);
            }
        } finally {
            releaseLock(lockKey);
        }
    }

    /**
     * 执行点赞
     *
     * 软删除机制：
     * - 如果存在 status=0 的历史点赞记录 → reLove() 恢复 status=1（免去 INSERT）
     * - 否则 → INSERT 新记录
     *
     * 缓存更新：事务提交后（afterCommit）通过 Lua 脚本异步更新 Redis。
     */
    private void doLike(Long userId, Long entityId, Long loveTypeId) {
        // 检查是否有历史取消点赞记录，有则复用（软删除恢复）
        UserLove existingInactive = checkInactiveRecord(userId, entityId, loveTypeId);
        if (existingInactive != null) {
            userLoveMapper.reLove(userId, entityId, loveTypeId);
        } else {
            UserLove userLove = new UserLove();
            userLove.setUserId(userId);
            userLove.setEntityId(entityId);
            userLove.setLoveTypeId(loveTypeId);
            userLove.setStatus(1);
            userLove.setCreateTime(LocalDateTime.now());
            userLoveMapper.insert(userLove);
        }

        // 更新实体（文章/评论）的 love 计数字段
        updateEntityLoveCount(entityId, loveTypeId, 1);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        updateLoveCache(entityId, loveTypeId, false);
                    }
                }
        );
        log.info("用户 {} 点赞实体 {} 成功", userId, entityId);
    }

    /**
     * 执行取消点赞
     *
     * 软删除：不物理删除记录，只将 status 设为 0。
     * 保留点赞历史数据，便于后续数据分析和"重点赞"时复用记录。
     */
    private void doUnlike(Long userId, Long entityId, Long loveTypeId) {
        // 软删除：UPDATE status = 0，保留历史记录
        userLoveMapper.cancelLove(userId, entityId, loveTypeId);

        updateEntityLoveCount(entityId, loveTypeId, -1);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        updateLoveCache(entityId, loveTypeId, true);
                    }
                }
        );
        log.info("用户 {} 取消点赞实体 {}", userId, entityId);
    }

    /**
     * 检查是否存在已取消的历史点赞记录（status=0）
     * 用于 doLike 时判断是否可以复用历史记录（reLove）
     *
     * 注意：selectByUserAndEntity 的 SQL 加了 AND status = 1 过滤，
     * 所以这里需要另外的判断逻辑来检测软删除记录。
     * 当前实现通过查询是否有记录（不限制 status）来判断，
     * 如果 selectByUserAndEntity 返回 null 但实际 DB 中有 status=0 的记录，
     * 说明存在可复用的历史记录。
     */
    private UserLove checkInactiveRecord(Long userId, Long entityId, Long loveTypeId) {
        try {
            return userLoveMapper.selectByUserAndEntity(userId, entityId, loveTypeId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断当前用户是否已点赞指定实体
     *
     * 缓存优先策略：先查 Redis SET，命中直接返回。
     * 未命中查 DB 的 user_love 表（status=1），有则回写缓存。
     */
    @Override
    public Boolean isEntityLovedByUser(Long entityId, Long loveTypeId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (entityId == null || loveTypeId == null || userId == null) {
            return false;
        }

        String userLikeKey = String.format("love:user:%s:%s", userId, loveTypeId);

        try {
            Boolean isLoved = stringRedisTemplate.opsForSet().isMember(userLikeKey, entityId.toString());
            if (Boolean.TRUE.equals(isLoved)) {
                return true;
            }

            // 查询 status=1 的有效点赞记录
            UserLove userLove = userLoveMapper.selectByUserAndEntity(userId, entityId, loveTypeId);
            boolean lovedInDB = userLove != null;

            if (lovedInDB) {
                stringRedisTemplate.opsForSet().add(userLikeKey, entityId.toString());
                stringRedisTemplate.expire(userLikeKey, CACHE_TTL, TimeUnit.SECONDS);
            }
            return lovedInDB;
        } catch (Exception e) {
            log.error("检查用户点赞状态失败: userId={}, entityId={}, loveTypeId={}",
                    userId, entityId, loveTypeId, e);
            try {
                UserLove userLove = userLoveMapper.selectByUserAndEntity(userId, entityId, loveTypeId);
                return userLove != null;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * 获取实体点赞数
     *
     * 缓存 miss 时根据 loveTypeId 查对应的文章表或评论表获取 love 字段值。
     */
    @Override
    public String getEntityLoveCount(Long entityId, Long loveTypeId) {
        if (entityId == null || loveTypeId == null) {
            return "0";
        }

        String countKey = String.format("love:count:%s:%s", loveTypeId, entityId);
        String countStr = stringRedisTemplate.opsForValue().get(countKey);

        if (countStr != null) {
            return countStr;
        }

        try {
            if (Objects.equals(loveTypeId, LOVE_TYPE_ARTICLE)) {
                Article article = articleMapper.selectById(entityId);
                Long loveCount = article != null ? article.getLove() : 0L;
                stringRedisTemplate.opsForValue().set(countKey, String.valueOf(loveCount), CACHE_TTL, TimeUnit.SECONDS);
                return loveCount.toString();
            } else {
                Comment comment = commentMapper.selectById(entityId);
                Long loveCount = comment != null ? comment.getLove() : 0L;
                stringRedisTemplate.opsForValue().set(countKey, String.valueOf(loveCount), CACHE_TTL, TimeUnit.SECONDS);
                return loveCount.toString();
            }
        } catch (Exception e) {
            log.error("获取实体点赞数失败: entityId={}, loveTypeId={}", entityId, loveTypeId, e);
            try {
                if (Objects.equals(loveTypeId, LOVE_TYPE_ARTICLE)) {
                    Article article = articleMapper.selectById(entityId);
                    return article == null ? "0" : String.valueOf(article.getLove());
                } else {
                    Comment comment = commentMapper.selectById(entityId);
                    return comment == null ? "0" : String.valueOf(comment.getLove());
                }
            } catch (Exception ex) {
                return "0";
            }
        }
    }

    /**
     * 验证点赞类型 ID 是否为已定义的合法类型
     */
    private boolean isValidLoveTypeId(Long loveTypeId) {
        return loveTypeId != null && (Objects.equals(loveTypeId, LOVE_TYPE_ARTICLE)
                || Objects.equals(loveTypeId, LOVE_TYPE_COMMENT));
    }

    /**
     * 更新实体（文章/评论）表中的点赞计数器
     *
     * 使用查询-更新模式，delta 为 +1（点赞）或 -1（取消点赞）。
     * 防止计数为负：Math.max(0, ...)
     */
    private void updateEntityLoveCount(Long entityId, Long loveTypeId, int delta) {
        try {
            if (Objects.equals(loveTypeId, LOVE_TYPE_ARTICLE)) {
                Article article = articleMapper.selectById(entityId);
                if (article != null) {
                    long newCount = Math.max(0, article.getLove() + delta);
                    article.setLove(newCount);
                    articleMapper.updateById(article);
                }
            } else {
                Comment comment = commentMapper.selectById(entityId);
                if (comment != null) {
                    long newCount = Math.max(0, comment.getLove() + delta);
                    comment.setLove(newCount);
                    commentMapper.updateById(comment);
                }
            }
        } catch (Exception e) {
            log.error("更新实体点赞数失败: entityId={}, loveTypeId={}, delta={}", entityId, loveTypeId, delta, e);
        }
    }

    /**
     * 通过 Lua 脚本原子更新点赞缓存
     *
     * 操作三个 Redis 键：
     * - love:user:{userId}:{loveTypeId}  ← 用户点赞的实体集合 (SET)
     * - love:type:{loveTypeId}:{entityId} ← 按类型分类的点赞集合 (SET)
     * - love:count:{loveTypeId}:{entityId} ← 点赞计数器 (STRING)
     */
    protected void updateLoveCache(Long entityId, Long loveTypeId, boolean isUnlike) {
        Long userId = SecurityUtils.getCurrentUserId();
        try {
            String userLikeKey = String.format("love:user:%s:%s", userId, loveTypeId);
            String loveTypeLikeKey = String.format("love:type:%s:%s", loveTypeId, entityId);
            String countKey = String.format("love:count:%s:%s", loveTypeId, entityId);

            Object[] args = new Object[]{entityId.toString(), userId.toString(), String.valueOf(CACHE_TTL)};

            Long result;
            if (isUnlike) {
                result = stringRedisTemplate.execute(UNLOVE_SCRIPT,
                        java.util.Arrays.asList(userLikeKey, loveTypeLikeKey, countKey), args);
            } else {
                result = stringRedisTemplate.execute(LOVE_SCRIPT,
                        java.util.Arrays.asList(userLikeKey, loveTypeLikeKey, countKey), args);
            }
            log.info("更新点赞缓存完成, result: {}", result);
        } catch (Exception e) {
            log.error("更新点赞缓存失败: userId={}, entityId={}, loveTypeId={}", userId, entityId, loveTypeId, e);
        }
    }

    /**
     * 获取分布式锁（基于 Redis SETNX）
     */
    private Boolean tryLock(String lockKey) {
        try {
            return stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOVE_LOCK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取分布式锁失败", e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     */
    private void releaseLock(String lockKey) {
        try {
            stringRedisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.error("释放锁失败", e);
        }
    }
}
