package com.example.my_blog.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.example.my_blog.config.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OSS 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OssProperties ossProperties;

    /**
     * 获取 OSS 上传凭证
     */
    public Map<String, String> getUploadPolicy() {
        try {
            String endpoint = ossProperties.getEndpoint();
            String accessKeyId = ossProperties.getAccessKeyId();
            String accessKeySecret = ossProperties.getAccessKeySecret();
            String bucket = ossProperties.getBucketName();
            String dir = ossProperties.getDirPrefix();
            long expireTime = ossProperties.getExpireTime();

            // 计算过期时间
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            // 构建 Policy 条件
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000); // 最大 1GB
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 创建 OSS 客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            // 生成 Policy
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            ossClient.shutdown();

            // 封装返回结果
            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", "https://" + bucket + "." + endpoint);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            log.info("OSS 上传凭证生成成功");
            return respMap;

        } catch (Exception e) {
            log.error("生成 OSS 上传凭证失败", e);
            throw new RuntimeException("生成上传凭证失败：" + e.getMessage());
        }
    }
}
