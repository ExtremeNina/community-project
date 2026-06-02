package com.example.onlyone.Service;

import com.example.onlyone.Entity.Article;
import com.example.onlyone.VO.RmArticleVO;

import java.util.List;

public interface RecommendService {


    List<Article> getFeedList(Long userId, int offset, int limit);

    List<Article> getHotFeed(int offset, int limit);
}
