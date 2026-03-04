package com.example.my_blog.dto;

import lombok.Data;
import java.util.List;

/**
 * 标签树节点DTO
 */
@Data
public class TagTreeNode {
    private Long id;
    private String name;
    private List<TagTreeNode> children;
}