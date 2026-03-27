package com.example.my_blog.service;

import com.example.my_blog.dto.CreateMainTagRequest;
import com.example.my_blog.dto.CreateSubTagRequest;
import com.example.my_blog.dto.DeleteTagRequest;
import com.example.my_blog.dto.UpdateMainTagRequest;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 创建主标签
     */
    Object createMainTag(CreateMainTagRequest request);
    
    /**
     * 创建小标签
     */
    Object createSubTag(CreateSubTagRequest request);
    
    /**
     * 删除标签
     */
    Object deleteTag(DeleteTagRequest request);
    
    /**
     * 获取所有标签树形结构
     */
    Object getAllTagsTree();
    
    /**
     * 更新主标签名称
     */
    Object updateMainTag(UpdateMainTagRequest request);
}