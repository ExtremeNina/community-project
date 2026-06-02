package com.example.onlyone.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlyone.Entity.OfflineNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OfflineNotificationMapper extends BaseMapper<OfflineNotification> {

    @Select("select * from offline_notification where user_id = #{userId} and is_sent = 0 order by created_at asc")
    List<OfflineNotification> selectByUserIdAndUnsent(Long userId);

    @org.apache.ibatis.annotations.Delete("delete from offline_notification where is_sent = 1 and created_at < date_sub(now(), interval #{days} day)")
    int deleteSentBeforeDays(int days);
}