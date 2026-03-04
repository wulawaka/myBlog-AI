package com.example.my_blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章响应DTO
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
    
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private String username;
        private String email;
    }
}