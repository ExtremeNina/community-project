package com.example.onlyone.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.ArticleAndLabel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleAndLabelMapper extends BaseMapper<ArticleAndLabel> {

    @Select("select * from article_and_label where article_id = #{articleId}")
    List<ArticleAndLabel> selectAll(Long articleId);

    @Delete("delete from article_and_label where article_id = #{articleId}")
    void deleteByArticleId(Long articleId);
}
