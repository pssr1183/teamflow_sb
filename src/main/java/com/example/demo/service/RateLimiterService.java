package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final int NO_OF_REQUESTS = 5;
    private static final Duration EXPIRY_WINDOW = Duration.ofMinutes(1);

    public boolean isAllowed(String key) {
        String redisKey = "rate_limit:"+key;
        Long noOfRequests = redisTemplate.opsForValue().increment(redisKey);
        if(noOfRequests == null) {
            return false;
        }
        if(noOfRequests == 1) {
            redisTemplate.expire(redisKey,EXPIRY_WINDOW);
        }
        return noOfRequests <= NO_OF_REQUESTS;
    }
}
