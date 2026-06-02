package com.example.onlyone.Service;

import com.example.onlyone.VO.SysReviewVO;

import java.util.List;

public interface SysReviewService {

    List<SysReviewVO> getPendingList(int page, int size, String type);

    void approve(Long recordId, String remark);

    void reject(Long recordId, String remark);
}