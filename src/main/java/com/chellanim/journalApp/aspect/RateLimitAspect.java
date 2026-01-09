package com.chellanim.journalApp.aspect;

import com.chellanim.journalApp.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Before("@annotation(rateLimit)")
  public void checkRateLimit(RateLimit rateLimit) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String ip = request.getRemoteAddr();
    String key = "rate_limit:" + request.getRequestURI() + ":" + ip;

    String currentAttempts = redisTemplate.opsForValue().get(key);

    if (currentAttempts != null && Integer.parseInt(currentAttempts) >= rateLimit.attempts()) {
      log.warn("Rate limit exceeded for IP: {} on URI: {}", ip, request.getRequestURI());
      throw new RuntimeException("Too many attempts. Please try again after " + rateLimit.windowMinutes() + " minutes.");
    }

    // Increment attempts
    Long count = redisTemplate.opsForValue().increment(key);
    if (count != null && count == 1) {
      redisTemplate.expire(key, rateLimit.windowMinutes(), TimeUnit.MINUTES);
    }

    log.info("Attempt {} for IP: {} on URI: {}", count, ip, request.getRequestURI());
  }
}