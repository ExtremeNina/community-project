package com.example.onlyone.Service;

import com.example.onlyone.VO.ArticleDetailVO;
import com.example.onlyone.VO.ArticleVO;
import com.github.pagehelper.PageInfo;

public interface ArticleService {


    ArticleDetailVO getArticleDetail(Long articleId);

    PageInfo<ArticleVO> getAllArticle();

    void updateArticleStatus(Long articleId, Integer status);
}
