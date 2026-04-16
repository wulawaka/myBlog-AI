package com.example.my_blog.constant;

/**
 * 文章模块错误码常量类
 * 
 * 错误码格式：XYYZZ（5 位数）
 * - 第 1 位 (X)：状态类型（2=成功，4=客户端错误，5=服务端错误）
 * - 第 2-3 位 (YY)：模块标识（02=文章模块）
 * - 第 4-5 位 (ZZ)：具体操作序号（从 01 开始）
 */
public class ArticleErrorCode {
    
    // ==================== 成功状态 (202XX) ====================
    /**
     * 操作成功
     */
    public static final int SUCCESS = 20201;
    
    // ==================== 客户端错误 (402XX) ====================
    /**
     * 文章不存在
     */
    public static final int ARTICLE_NOT_FOUND = 40201;
    
    /**
     * 文章已被删除
     */
    public static final int ARTICLE_DELETED = 40202;
    
    /**
     * 无权访问文章
     */
    public static final int ARTICLE_UNAUTHORIZED = 40203;
    
    /**
     * 分类不存在
     */
    public static final int CATEGORY_NOT_FOUND = 40204;
    
    /**
     * 分类必须是主标签
     */
    public static final int CATEGORY_MUST_BE_MAIN = 40205;
    
    /**
     * 子标签不存在
     */
    public static final int SUB_CATEGORY_NOT_FOUND = 40206;
    
    /**
     * 子标签不能是主标签
     */
    public static final int SUB_CATEGORY_CANNOT_BE_MAIN = 40207;
    
    /**
     * 子标签数量超过限制
     */
    public static final int SUB_CATEGORY_COUNT_EXCEEDED = 40208;
    
    /**
     * 参数无效
     */
    public static final int INVALID_PARAM = 40209;
    
    /**
     * 置顶状态无效
     */
    public static final int INVALID_TOP_STATUS = 40210;
    
    /**
     * 更新文章成功
     */
    public static final int UPDATE_SUCCESS = 20202;
    
    // ==================== 服务端错误 (502XX) ====================
    /**
     * 服务器内部错误
     */
    public static final int SERVER_ERROR = 50201;
    
    /**
     * 数据库错误
     */
    public static final int DATABASE_ERROR = 50202;
}
