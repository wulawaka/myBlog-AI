package com.example.my_blog.constant;

/**
 * 分类模块错误码常量类
 * 
 * 错误码格式：XYYZZ（5 位数）
 * - 第 1 位 (X)：状态类型（2=成功，4=客户端错误，5=服务端错误）
 * - 第 2-3 位 (YY)：模块标识（03=分类模块）
 * - 第 4-5 位 (ZZ)：具体操作序号（从 01 开始）
 */
public class CategoryErrorCode {
    
    // ==================== 成功状态 (203XX) ====================
    /**
     * 操作成功
     */
    public static final int SUCCESS = 20301;
    
    // ==================== 客户端错误 (403XX) ====================
    /**
     * 分类不存在
     */
    public static final int CATEGORY_NOT_FOUND = 40301;
    
    /**
     * 分类名称已存在
     */
    public static final int CATEGORY_NAME_EXISTS = 40302;
    
    /**
     * 父标签不存在
     */
    public static final int PARENT_TAG_NOT_FOUND = 40303;
    
    /**
     * 标签已被删除
     */
    public static final int TAG_DELETED = 40304;
    
    /**
     * 存在未删除的子标签
     */
    public static final int HAS_ACTIVE_CHILD_TAGS = 40305;
    
    /**
     * 参数无效
     */
    public static final int INVALID_PARAM = 40306;
    
    // ==================== 服务端错误 (503XX) ====================
    /**
     * 服务器内部错误
     */
    public static final int SERVER_ERROR = 50301;
    
    /**
     * 数据库错误
     */
    public static final int DATABASE_ERROR = 50302;
}
