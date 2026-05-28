package com.novaflow.auth.infra.external.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final StringRedisTemplate redisTemplate;
    private static final String CODE_PREFIX = "sms:code:";
    private static final int CODE_EXPIRATION_MINUTES = 5;

    public SmsServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendVerificationCode(String phone) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 存储到Redis，5分钟过期
        String key = CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // TODO: 调用阿里云SMS服务发送验证码
        log.info("发送验证码: phone={}, code={}", phone, code);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        boolean valid = storedCode.equals(code);
        if (valid) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
        }

        return valid;
    }
}
