package com.example.onlyone.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 离线消息表
 * <p>
 * 用于存储需要推送给用户但用户当前不在线的消息。
 * 定时任务扫描 is_sent=0 的记录进行推送，推送成功后标记为已发送。
 */
@Data
public class OfflineNotification {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收用户 ID */
    private Long userId;

    /** 消息类型，如 moderation_result（审核结果通知） */
    private String messageType;

    /** 推送内容体，JSON 格式，包含结果状态、原因等 */
    private String messageContent;

    /** 是否已推送：0 未推送，1 已推送 */
    private Integer isSent;

    /** 创建时间 */
    private LocalDateTime createdAt;
}