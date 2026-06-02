package com.example.onlyone.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户点赞实体
 *
 * 软删除设计：
 * status = 1 表示有效点赞，status = 0 表示已取消点赞（软删除）。
 * 取消点赞时不物理删除记录，保留用户行为历史：
 * - 数据分析：可统计用户点赞/取消点赞的行为模式
 * - 重点赞优化：用户再次点赞同一实体时，reLove() 直接恢复 status=1，避免 INSERT
 *
 * user_id + entity_id + love_type_id + status=1 构成业务唯一约束。
 */
@Data
public class UserLove {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long entityId;
    private LocalDateTime createTime;
    private Long loveTypeId;
    private Integer status;
}
