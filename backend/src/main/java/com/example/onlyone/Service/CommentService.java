package com.example.onlyone.Service;

import com.example.onlyone.DTO.CommentDTO;
import com.example.onlyone.VO.CommentVO;
import com.example.onlyone.VO.UserProfileVO;
import com.github.pagehelper.PageInfo;


public interface CommentService {

    UserProfileVO getUserProfile();

    CommentVO publishComment(CommentDTO commentDTO);

    PageInfo<CommentVO> getCommentList(Long articleId);
}
