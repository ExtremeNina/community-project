package com.example.onlyone.Service.ServiceImpl;

import com.example.onlyone.DTO.ModerationResult;
import com.example.onlyone.DTO.ModerationTask;
import com.example.onlyone.Entity.Article;
import com.example.onlyone.Entity.Comment;
import com.example.onlyone.Entity.ContentModerationRecord;
import com.example.onlyone.Mapper.ArticleMapper;
import com.example.onlyone.Mapper.CommentMapper;
import com.example.onlyone.Mapper.ContentModerationRecordMapper;
import com.example.onlyone.Service.AliyunModerationService;
import com.example.onlyone.Service.ModerationService;
import com.example.onlyone.websocket.ForumWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ModerationServiceImpl implements ModerationService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AliyunModerationService aliyunModerationService;

    @Resource
    private ModerationDecider moderationDecider;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ContentModerationRecordMapper contentModerationRecordMapper;

    @Lazy
    @Resource
    private ForumWebSocketHandler forumWebSocketHandler;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void submitTask(ModerationTask task) {
        rabbitTemplate.convertAndSend("moderation.exchange", "moderation.request", task);
        log.info("审核任务已提交: targetType={}, targetId={}", task.getTargetType(), task.getTargetId());
    }

    @RabbitListener(queues = "moderation.request.queue")
    @Transactional
    public void handleRequest(ModerationTask task) {
        log.info("处理审核任务: targetType={}, targetId={}", task.getTargetType(), task.getTargetId());

        try {
            ModerationResult result = aliyunModerationService.checkText(task.getContent());
            String businessStatus = moderationDecider.decideBusinessStatus(result);
            String recordStatus = moderationDecider.decideRecordStatus(result);

            updateBusinessStatus(task.getTargetType(), task.getTargetId(), businessStatus);
            insertModerationRecord(task, result, recordStatus);
            sendResultNotification(task, businessStatus, recordStatus, result.getReason());

            log.info("审核完成: targetType={}, targetId={}, status={}", task.getTargetType(), task.getTargetId(), recordStatus);
        } catch (Exception e) {
            log.error("审核处理异常: targetType={}, targetId={}", task.getTargetType(), task.getTargetId(), e);
        }
    }

    private void updateBusinessStatus(String targetType, Long targetId, String status) {
        long statusVal = Long.parseLong(status);
        if ("article".equals(targetType)) {
            Article article = new Article();
            article.setId(targetId);
            article.setStatus(statusVal);
            articleMapper.updateById(article);
        } else if ("comment".equals(targetType)) {
            Comment comment = new Comment();
            comment.setId(targetId);
            comment.setStatus(statusVal);
            commentMapper.updateById(comment);
        }
    }

    private void insertModerationRecord(ModerationTask task, ModerationResult result, String recordStatus) {
        ContentModerationRecord record = new ContentModerationRecord();
        record.setTargetType(task.getTargetType());
        record.setTargetId(task.getTargetId());
        record.setStatus(recordStatus);
        record.setModelScore(result.getConfidence() != null ? result.getConfidence() : BigDecimal.ZERO);
        record.setModelLabels(result.getLabels() != null ? result.getLabels().toString() : null);
        record.setModelReason(result.getReason());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        contentModerationRecordMapper.insert(record);
    }

    private void sendResultNotification(ModerationTask task, String businessStatus, String recordStatus, String reason) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("targetType", task.getTargetType());
            data.put("targetId", task.getTargetId());
            data.put("status", recordStatus.startsWith("auto_approved") ? "approved" :
                    recordStatus.startsWith("auto_rejected") ? "rejected" : "human_review");
            data.put("reason", reason);

            Map<String, Object> message = new HashMap<>();
            message.put("type", "moderation_result");
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());

            String jsonMsg = objectMapper.writeValueAsString(message);
            forumWebSocketHandler.pushModerationResult(task.getUserId(), jsonMsg);
        } catch (Exception e) {
            log.error("发送审核结果通知失败: userId={}", task.getUserId(), e);
        }
    }
}