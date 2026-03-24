package com.example.my_blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

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
    private String subCategoryIds;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 作者用户名
     */
    private String username;
}