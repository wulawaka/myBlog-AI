package com.example.my_blog.controller;

import com.example.my_blog.dto.CreateMainTagRequest;
import com.example.my_blog.dto.CreateSubTagRequest;
import com.example.my_blog.dto.DeleteTagRequest;
import com.example.my_blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 创建主标签接口
     * @param name 标签名称（必填，1-6位）
     * @return 创建结果JSON
     */
    @PostMapping("/main-tag")
    public Object createMainTag(@Valid @RequestBody CreateMainTagRequest request) {
        log.info("收到创建主标签请求，标签名称：{}", request.getName());
        return categoryService.createMainTag(request);
    }

    /**
     * 创建小标签接口
     * @param parentId 父标签ID（必填，必须存在）
     * @param name 标签名称（必填，1-6位，不能重复）
     * @return 创建结果JSON
     */
    @PostMapping("/sub-tag")
    public Object createSubTag(@Valid @RequestBody CreateSubTagRequest request) {
        log.info("收到创建小标签请求，父标签ID：{}，标签名称：{}", request.getParentId(), request.getName());
        return categoryService.createSubTag(request);
    }

    /**
     * 删除标签接口
     * @param name 标签名称（必填）
     * @return 删除结果JSON
     */
    @DeleteMapping("/tag")
    public Object deleteTag(@Valid @RequestBody DeleteTagRequest request) {
        log.info("收到删除标签请求，标签名称：{}", request.getName());
        return categoryService.deleteTag(request);
    }

    /**
     * 获取所有标签树形结构接口
     * @return 标签树JSON
     */
    @GetMapping("/tags/tree")
    public Object getAllTagsTree() {
        log.info("收到获取标签树请求");
        return categoryService.getAllTagsTree();
    }
}