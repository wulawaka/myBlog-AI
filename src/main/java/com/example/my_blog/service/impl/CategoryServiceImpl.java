package com.example.my_blog.service.impl;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.CreateMainTagRequest;
import com.example.my_blog.dto.CreateSubTagRequest;
import com.example.my_blog.dto.DeleteTagRequest;
import com.example.my_blog.dto.TagTreeNode;
import com.example.my_blog.entity.Category;
import com.example.my_blog.repository.CategoryRepository;
import com.example.my_blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Object createMainTag(CreateMainTagRequest request) {
        try {
            // 检查标签名称是否已存在
            if (categoryRepository.existsByName(request.getName())) {
                return ApiResponse.error("标签名称已存在");
            }

            // 创建主标签（parent_id = 0）
            Category category = new Category();
            category.setName(request.getName());
            category.setParentId(0L); // 主标签parent_id默认为0
            category.setIsDeleted(0); // 默认未删除
            
            // 保存到数据库
            Category savedCategory = categoryRepository.save(category);
            
            // 返回成功信息
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedCategory.getId());
            result.put("name", savedCategory.getName());
            result.put("parentId", savedCategory.getParentId());
            result.put("createdAt", savedCategory.getCreatedAt());

            log.info("主标签 {} 创建成功", savedCategory.getName());
            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("创建主标签异常", e);
            return ApiResponse.error("创建主标签失败：" + e.getMessage());
        }
    }

    @Override
    public Object createSubTag(CreateSubTagRequest request) {
        try {
            // 检查父标签是否存在
            if (!categoryRepository.existsById(request.getParentId())) {
                return ApiResponse.error("父标签不存在");
            }
            
            // 检查标签名称是否已存在
            if (categoryRepository.existsByName(request.getName())) {
                return ApiResponse.error("标签名称已存在");
            }

            // 创建小标签
            Category category = new Category();
            category.setName(request.getName());
            category.setParentId(request.getParentId()); // 使用传入的parent_id
            category.setIsDeleted(0); // 默认未删除
            
            // 保存到数据库
            Category savedCategory = categoryRepository.save(category);
            
            // 返回成功信息
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedCategory.getId());
            result.put("name", savedCategory.getName());
            result.put("parentId", savedCategory.getParentId());
            result.put("createdAt", savedCategory.getCreatedAt());

            log.info("小标签 {} 创建成功，父标签ID: {}", savedCategory.getName(), savedCategory.getParentId());
            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("创建小标签异常", e);
            return ApiResponse.error("创建小标签失败：" + e.getMessage());
        }
    }

    @Override
    public Object deleteTag(DeleteTagRequest request) {
        try {
            // 根据名称查找标签
            Optional<Category> categoryOptional = categoryRepository.findByName(request.getName());
            
            if (categoryOptional.isEmpty()) {
                return ApiResponse.error("标签不存在");
            }

            Category category = categoryOptional.get();
            Long categoryId = category.getId();
            Long parentId = category.getParentId();
            Integer isDeleted = category.getIsDeleted();

            // 检查标签是否已经被删除
            if (isDeleted == 1) {
                return ApiResponse.error("标签已被删除");
            }

            // 判断是否为主标签(parentId = 0)
            if (parentId == 0) {
                // 检查是否有子标签引用此主标签
                List<Category> childCategories = categoryRepository.findByParentId(categoryId);
                
                if (!childCategories.isEmpty()) {
                    // 检查子标签中是否有未删除的(isDeleted = 0)
                    boolean hasActiveChild = childCategories.stream()
                            .anyMatch(child -> child.getIsDeleted() == 0);
                    
                    if (hasActiveChild) {
                        // 存在未删除的子标签，无法删除主标签
                        return ApiResponse.error("该主标签下存在未删除的子标签，无法删除");
                    }
                    // 所有子标签都已删除，可以删除主标签
                }
                
                // 可以删除，设置isDeleted = 1
                category.setIsDeleted(1);
                categoryRepository.save(category);
                
                log.info("主标签 {} 删除成功", category.getName());
                return ApiResponse.success("主标签删除成功");
                
            } else {
                // 小标签，直接删除
                category.setIsDeleted(1);
                categoryRepository.save(category);
                
                log.info("小标签 {} 删除成功", category.getName());
                return ApiResponse.success("小标签删除成功");
            }

        } catch (Exception e) {
            log.error("删除标签异常", e);
            return ApiResponse.error("删除标签失败：" + e.getMessage());
        }
    }

    @Override
    public Object getAllTagsTree() {
        try {
            // 获取所有未删除的标签
            List<Category> allCategories = categoryRepository.findByIsDeletedOrderByCreatedAtDesc(0);
            
            // 分离主标签和子标签
            List<Category> mainTags = allCategories.stream()
                    .filter(category -> category.getParentId() == 0)
                    .collect(Collectors.toList());
            
            List<Category> subTags = allCategories.stream()
                    .filter(category -> category.getParentId() != 0)
                    .collect(Collectors.toList());
            
            // 构建树形结构
            List<TagTreeNode> treeNodes = mainTags.stream()
                    .map(mainTag -> {
                        TagTreeNode mainNode = convertToTreeNode(mainTag);
                        // 为每个主标签添加子标签
                        List<TagTreeNode> children = subTags.stream()
                                .filter(subTag -> subTag.getParentId().equals(mainTag.getId()))
                                .map(this::convertToTreeNode)
                                .collect(Collectors.toList());
                        mainNode.setChildren(children);
                        return mainNode;
                    })
                    .collect(Collectors.toList());
            
            return ApiResponse.success(treeNodes);
            
        } catch (Exception e) {
            log.error("获取标签树异常", e);
            return ApiResponse.error("获取标签树失败：" + e.getMessage());
        }
    }
    
    /**
     * 将Category转换为TagTreeNode
     */
    private TagTreeNode convertToTreeNode(Category category) {
        TagTreeNode node = new TagTreeNode();
        node.setId(category.getId());
        node.setName(category.getName());
        return node;
    }
}