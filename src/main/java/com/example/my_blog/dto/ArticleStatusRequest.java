package com.example.my_blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分页查询文章状态请求 DTO
 */
@Data
public class ArticleStatusRequest {

    /**
     * 页码（可选，默认 1）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量（可选，默认 10）
     */
    private Integer pageSize = 10;

    /**
     * 文章 ID（可选，用于指定查询某篇文章的状态）
     */
    private Long articleId;

    /**
     * 草稿状态（可选，默认 0-已发布，1-草稿）
     */
    private Integer isDraft;

    /**
     * 删除状态（可选，0-否，1-是）
     */
    private Integer isDeleted;
}
