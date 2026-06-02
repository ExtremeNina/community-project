package com.example.onlyone.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 敏感词库 Mapper
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

    /**
     * 查询所有敏感词
     * <p>
     * 用于 AC 自动机初始化时加载全量敏感词库。
     *
     * @return 敏感词列表
     */
    @Select("SELECT id, word, level, created_at FROM sensitive_word")
    List<SensitiveWord> selectList();
}