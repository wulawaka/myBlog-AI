package com.example.my_blog.controller;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * OSS 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OssController {

    private final OssService ossService;

    /**
     * 获取 OSS 上传凭证接口（无需登录）
     * @return 上传凭证信息
     */
    @GetMapping("/upload-policy")
    public Object getUploadPolicy() {
        log.info("收到获取 OSS 上传凭证请求");
        try {
            Map<String, String> policy = ossService.getUploadPolicy();
            return ApiResponse.success(policy);
        } catch (Exception e) {
            log.error("获取 OSS 上传凭证失败", e);
            return ApiResponse.error("获取上传凭证失败：" + e.getMessage());
        }
    }
}
