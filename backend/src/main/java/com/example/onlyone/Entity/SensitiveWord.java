package com.example.onlyone.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏感词库表
 * <p>
 * 存储系统敏感词列表，用于 AC 自动机在内容发布时进行敏感词过滤。
 * 支持按严重等级分级管理。
 */
@Data
public class SensitiveWord {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 敏感词 */
    private String word;

    /** 严重等级，数值越大越严重 */
    private Integer level;

    /** 创建时间 */
    private LocalDateTime createdAt;
}