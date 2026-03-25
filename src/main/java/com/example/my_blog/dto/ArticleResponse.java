package com.example.my_blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章响应 DTO
 */
@Data
public class ArticleResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String summary;
    private String content;
    private Integer isTop;
    private Integer isDraft;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 作者用户名
     */
    private String username;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 子标签列表
     */
    private List<SubCategoryInfo> subCategories;
}