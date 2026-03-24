package com.example.my_blog.dto;

import lombok.Data;

/**
 * 子标签信息 DTO
 */
@Data
public class SubCategoryInfo {
    /**
     * 子标签 ID
     */
    private Long id;
    
    /**
     * 子标签名称
     */
    private String name;
}
