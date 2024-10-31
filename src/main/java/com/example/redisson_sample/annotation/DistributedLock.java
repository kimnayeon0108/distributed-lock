package com.example.redisson_sample.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * lock 이름
     */
    String key();

    /**
     * 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 획득을 위해 대기하는 시간 (default 5s)
     */
    long waitTime() default 5L;

    /**
     * 락 점유 시간 (default 3s)
     */
    long leaseTime() default 3L;
}
