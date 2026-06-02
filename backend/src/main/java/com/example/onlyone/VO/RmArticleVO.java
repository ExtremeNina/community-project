package com.example.onlyone.VO;


import lombok.Data;


//首页推荐流返回数据
@Data
public class RmArticleVO {

    //文章id
    private Long articleId;
    //标题
    private String title;
    //作者
    private String author;
    //封面页
    private String coverImageUrl;
    //点赞数
    private Long loveCount;

}
