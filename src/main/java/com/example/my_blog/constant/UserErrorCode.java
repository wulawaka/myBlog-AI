package com.example.my_blog.constant;

/**
 * 用户模块错误码常量类
 * 
 * 错误码格式：XYYZZ（5 位数）
 * - 第 1 位 (X)：状态类型（2=成功，4=客户端错误，5=服务端错误）
 * - 第 2-3 位 (YY)：模块标识（01=用户模块）
 * - 第 4-5 位 (ZZ)：具体操作序号（从 01 开始）
 */
public class UserErrorCode {
    
    // ==================== 成功状态 (201XX) ====================
    /**
     * 操作成功
     */
    public static final int SUCCESS = 20101;
    
    // ==================== 客户端错误 (401XX) ====================
    /**
     * 用户不存在
     */
    public static final int USER_NOT_FOUND = 40101;
    
    /**
     * 用户名或密码错误
     */
    public static final int CREDENTIAL_ERROR = 40102;
    
    /**
     * 用户名已存在
     */
    public static final int USERNAME_ALREADY_EXISTS = 40103;
    
    /**
     * 邮箱已存在
     */
    public static final int EMAIL_ALREADY_EXISTS = 40104;
    
    /**
     * 参数无效
     */
    public static final int INVALID_PARAM = 40105;
    
    /**
     * 未授权访问
     */
    public static final int UNAUTHORIZED = 40106;
    
    /**
     * 旧密码错误
     */
    public static final int OLD_PASSWORD_ERROR = 40107;
    
    /**
     * 账号已被禁用
     */
    public static final int ACCOUNT_DISABLED = 40108;
    
    /**
     * Token 无效或已过期
     */
    public static final int TOKEN_INVALID = 40109;
    
    // ==================== 服务端错误 (501XX) ====================
    /**
     * 服务器内部错误
     */
    public static final int SERVER_ERROR = 50101;
    
    /**
     * 数据库错误
     */
    public static final int DATABASE_ERROR = 50102;
    
    /**
     * 生成 Token 失败
     */
    public static final int TOKEN_GENERATION_ERROR = 50103;
    
    /**
     * 加密失败
     */
    public static final int ENCRYPTION_ERROR = 50104;
}
