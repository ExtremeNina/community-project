package com.example.onlyone.Controller;

import com.example.onlyone.Service.SysReviewService;
import com.example.onlyone.VO.SysReviewVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/review")
public class SysReviewController {

    @Resource
    private SysReviewService sysReviewService;

    @GetMapping("/pending")
    public List<SysReviewVO> getPendingList(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "article") String type) {
        return sysReviewService.getPendingList(page, size, type);
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long recordId, @RequestParam(defaultValue = "") String remark) {
        sysReviewService.approve(recordId, remark);
        return "审核通过";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long recordId, @RequestParam(defaultValue = "") String remark) {
        sysReviewService.reject(recordId, remark);
        return "已拦截";
    }
}