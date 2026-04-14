package com.example.my_blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录尝试管理服务（防止暴力破解）
 */
@Slf4j
@Service
public class LoginAttemptService {

    /**
     * 最大失败次数
     */
    private static final int MAX_FAILED_ATTEMPTS = 3;

    /**
     * 锁定时间（毫秒）- 15分钟
     */
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000;

    /**
     * 存储登录失败次数：key=username, value=AtomicInteger
     */
    private final ConcurrentHashMap<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();

    /**
     * 存储账户锁定时间：key=username, value=锁定到期时间戳
     */
    private final ConcurrentHashMap<String, Long> lockUntilTimes = new ConcurrentHashMap<>();

    /**
     * 检查账户是否被锁定
     *
     * @param username 用户名
     * @return true-已锁定，false-未锁定
     */
    public boolean isLocked(String username) {
        Long lockUntilTime = lockUntilTimes.get(username);
        if (lockUntilTime == null) {
            return false;
        }

        // 检查是否在锁定期内
        if (System.currentTimeMillis() < lockUntilTime) {
            long remainingTime = (lockUntilTime - System.currentTimeMillis()) / 1000 / 60;
            AtomicInteger count = failedAttempts.get(username);
            int failedCount = count != null ? count.get() : 0;
            log.warn("用户 {} 账户已被锁定，剩余 {} 分钟，失败次数：{}", 
                     username, remainingTime, failedCount);
            return true;
        }

        // 锁定期已过，清除记录
        lockUntilTimes.remove(username);
        failedAttempts.remove(username);
        log.info("用户 {} 锁定期已过，自动解锁", username);
        return false;
    }

    /**
     * 记录登录失败
     *
     * @param username 用户名
     */
    public void recordFailedAttempt(String username) {
        AtomicInteger count = failedAttempts.computeIfAbsent(username, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        log.warn("【登录失败记录】用户 {} 登录失败，当前失败次数：{}，最大允许次数：{}", 
                 username, currentCount, MAX_FAILED_ATTEMPTS);

        // 如果达到最大失败次数，锁定账户
        if (currentCount >= MAX_FAILED_ATTEMPTS) {
            long lockUntilTime = System.currentTimeMillis() + LOCK_TIME_DURATION;
            lockUntilTimes.put(username, lockUntilTime);
            log.error("【账户锁定】用户 {} 登录失败次数过多（{}次），账户已锁定 15 分钟，锁定至：{}", 
                     username, currentCount, new Date(lockUntilTime));
        }
    }

    /**
     * 登录成功，清除记录
     *
     * @param username 用户名
     */
    public void clearAttempts(String username) {
        AtomicInteger removed = failedAttempts.remove(username);
        lockUntilTimes.remove(username);
        
        if (removed != null) {
            log.info("用户 {} 登录成功，清除登录尝试记录（之前失败次数：{}）", 
                     username, removed.get());
        } else {
            log.info("用户 {} 登录成功，无登录尝试记录", username);
        }
    }

    /**
     * 获取剩余失败次数
     *
     * @param username 用户名
     * @return 剩余可尝试次数
     */
    public int getRemainingAttempts(String username) {
        AtomicInteger count = failedAttempts.get(username);
        if (count == null) {
            return MAX_FAILED_ATTEMPTS;
        }
        int currentCount = count.get();
        int remaining = Math.max(0, MAX_FAILED_ATTEMPTS - currentCount);
        log.debug("用户 {} 剩余尝试次数：{}，已失败次数：{}", 
                  username, remaining, currentCount);
        return remaining;
    }

    /**
     * 定时清理过期的登录尝试记录（每 30 分钟执行一次）
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanExpiredAttempts() {
        long now = System.currentTimeMillis();
        int beforeSize = lockUntilTimes.size();

        // 清理锁定期已过的记录
        lockUntilTimes.entrySet().removeIf(entry -> {
            return now >= entry.getValue();
        });

        // 同步清理 failedAttempts 中已解锁的用户
        failedAttempts.keySet().removeIf(username -> !lockUntilTimes.containsKey(username));

        int cleanedCount = beforeSize - lockUntilTimes.size();
        if (cleanedCount > 0) {
            log.info("定时清理登录尝试记录，共清理 {} 条过期记录", cleanedCount);
        }
    }
}
