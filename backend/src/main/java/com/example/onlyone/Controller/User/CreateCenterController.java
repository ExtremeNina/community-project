package com.example.onlyone.Controller.User;


import com.example.onlyone.DTO.ArticleDTO;
import com.example.onlyone.VO.ArticleVO;
import com.example.onlyone.VO.ReviewVO;
import com.example.onlyone.Common.Result;
import com.example.onlyone.Entity.Article;
import com.example.onlyone.Service.CreateCenterService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/create-center")
public class CreateCenterController {

    @Resource
    private CreateCenterService createCenterService;


    //发布文章
    @PostMapping
    public Result publishArticle(@RequestBody ArticleDTO articleDTO) {
        createCenterService.publishArticle(articleDTO);
        return Result.success();
    }

    //上传图片
    @PostMapping("/upload/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String,String> map =  createCenterService.uploadImage(file);
        String ImageUrl = map.get("url");
        return Result.success(ImageUrl);
    }

    //删除图片
    @PostMapping("/upload/image/delete")
    public Result deleteImage(@RequestParam("imageUrl") String imageUrl) {
        createCenterService.deleteImage(imageUrl);
        return Result.success();
    }

    //保存到草稿箱
    @PostMapping("/draft")
    public Result draftArticle(@RequestBody ArticleDTO articleDTO) {
        createCenterService.draftArticle(articleDTO);
        return Result.success();
    }

    //查看草稿箱
    @GetMapping("/drafts")
    public Result selectDraft() {
        List<ArticleVO> DraftList = createCenterService.selectDraft();
        return Result.success(DraftList);
    }

    //查询已发布文章
    @GetMapping("/published")
    public Result queryPublishArticle() {
        List<ArticleVO> publishList = createCenterService.queryPublish();
        return Result.success(publishList);
    }

    //查询待审核文章
    @GetMapping("/pending")
    public Result reviewArticle() {
        List<ArticleVO> reviewList = createCenterService.queryReview();
        return Result.success(reviewList);
    }

    //查看全部文章（所有状态）
    @GetMapping("/all")
    public Result allArticles() {
        List<ArticleVO> articleList = createCenterService.selectAll();
        return Result.success(articleList);
    }

    //查询未通过文章
    @GetMapping("/rejected")
    public Result queryRejectedArticle() {
        List<ArticleVO> rejectedList = createCenterService.queryRejected();
        return Result.success(rejectedList);
    }

    //编辑草稿箱文章同时可以编辑已发布的文章
    @PutMapping("/drafts/{draftId}")
    public Result modifyDraft(@PathVariable Long draftId) {
        Article draft =  createCenterService.modifyDraft(draftId);
        if(draft == null) {
            return Result.error("编辑文章失败");
        }
        return Result.success(draft);
    }

    //删除草稿箱文章
    @DeleteMapping("/drafts/{draftId}")
    public Result deleteDraft(@PathVariable Long draftId) {
        createCenterService.deleteDraft(draftId);
        return Result.success();
    }

    //删除已发布文章
    @DeleteMapping("/{articleId}")
    public Result deletePublishArticle(@PathVariable Long articleId) {
        createCenterService.deletePublishArticle(articleId);
        return Result.success();
    }

    //查询一篇待审核中的文章
    //可以查看待审核和已发布的文章，误打误撞，可以少写一个接口
    @GetMapping("/detail/{articleId}")
    public Result queryOneReviewArticle(@PathVariable Long articleId) {

        Map<ReviewVO, List<String>> articleMap = createCenterService.queryOneReviewArticle(articleId);
        // 转换为更清晰的结构
        Map<String, Object> result = new HashMap<>();
        if (!articleMap.isEmpty()) {
            Map.Entry<ReviewVO, List<String>> entry = articleMap.entrySet().iterator().next();
            result.put("article", entry.getKey());
            result.put("tags", entry.getValue());
        }
        return Result.success(result);
    }


    // 根据标题查询 - 使用查询参数筛选
    @GetMapping("/by-title")
    public Result selectByTitle(String title,Integer status) {
        List<ArticleVO> articleVOList = createCenterService.selectByTitle(title,status);
        return Result.success(articleVOList);
    }

    // 根据日期查询 - 使用查询参数筛选
    @GetMapping("/by-date")
    public Result<List<ArticleVO>> getArticlesByDate(
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "1") Integer status) {
        List<ArticleVO> articles = createCenterService.selectByDate(date, status);
        return Result.success(articles);
    }

    // ============================================================
    // 通用编辑回显接口（支持草稿/待审核/已发布）
    // ============================================================
    @GetMapping("/edit/{articleId}")
    public Result getArticleForEdit(@PathVariable Long articleId) {
        Map<String, Object> result = createCenterService.getArticleForEdit(articleId);
        return Result.success(result);
    }

    // ============================================================
    // 获取所有分类列表
    // ============================================================
    @GetMapping("/categories")
    public Result getAllCategories() {
        List<Map<String, Object>> categories = createCenterService.getAllCategories();
        return Result.success(categories);
    }

}
