package com.example.onlyone.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.ContentModerationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ContentModerationRecordMapper extends BaseMapper<ContentModerationRecord> {

    @Select("select * from content_moderation_record where status = #{status} and target_type = #{targetType} order by created_at asc limit #{offset}, #{limit}")
    List<ContentModerationRecord> selectByStatusAndType(@Param("status") String status,
                                                         @Param("targetType") String targetType,
                                                         @Param("offset") int offset,
                                                         @Param("limit") int limit);
}