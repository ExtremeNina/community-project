package com.example.onlyone.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.UserAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserActionMapper extends BaseMapper<UserAction> {

    @Select("SELECT DISTINCT user_id, article_id FROM user_action " +
            "WHERE action_type IN ('点赞','收藏') " +
            "AND create_time >= #{startDate}")
    List<Map<String, Long>> selectPositiveActions(@Param("startDate") LocalDateTime startDate);


    @Select("SELECT DISTINCT article_id FROM (" +
            "  SELECT article_id, create_time " +
            "  FROM user_action " +
            "  WHERE user_id = #{userId} " +
            "    AND action_type IN ('点赞','收藏') " +
            "    AND create_time >= #{since} " +
            "  ORDER BY create_time DESC" +
            ") AS t LIMIT 20")
    List<Long> selectRecentLikedNotes(@Param("userId") Long userId,
                                      @Param("since") LocalDateTime since);



    //计算一周内热度最高的笔记（500上限）
    @Select("SELECT article_id AS articleId, SUM(weight) AS hot " +
            "FROM user_action " +
            "WHERE create_time >= #{since} " +
            "GROUP BY articleId " +
            "ORDER BY hot DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectHotNotes(@Param("since") LocalDateTime since,
                                             @Param("limit") int limit);


    // 去重：获取用户所有已交互笔记ID
    @Select("SELECT DISTINCT article_id FROM user_action WHERE user_id = #{userId}")
    List<Long> selectInteractedNoteIds(@Param("userId") Long userId);

}
