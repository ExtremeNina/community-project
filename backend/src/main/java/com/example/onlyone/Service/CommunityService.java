package com.example.onlyone.Service;

import com.example.onlyone.VO.RecommendVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface CommunityService {


    Map<String,Object> getIconAndId();

    boolean isLogin(HttpServletRequest request);

    List<RecommendVO> getReArticles();

    Long getUnreadCount(Long userId);
}
