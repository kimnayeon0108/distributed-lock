package com.example.redisson_sample.aspect;

import com.example.redisson_sample.annotation.DistributedLock;
import com.example.redisson_sample.component.AopForTransaction;
import com.example.redisson_sample.exception.CustomException;
import com.example.redisson_sample.util.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.redisson_sample.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        RLock rLock = redissonClient.getLock(key);
        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                throw new CustomException("(락 획득 실패) 동시성 이슈가 발생했습니다.");
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.error("InterruptedException");
            throw new CustomException(e.getMessage());
        } catch (RedisException e) {
            e.printStackTrace();
            log.error("RedisException");
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        } finally {
            try {
                rLock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
