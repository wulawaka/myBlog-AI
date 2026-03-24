package com.example.my_blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表项 DTO
 */
@Data
public class ArticleListItem {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String summary;
    private Integer isTop;
    private LocalDateTime updatedAt;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 作者用户名
     */
    private String username;
    
    /**
     * 子标签列表
     */
    private List<SubCategoryInfo> subCategories;
}