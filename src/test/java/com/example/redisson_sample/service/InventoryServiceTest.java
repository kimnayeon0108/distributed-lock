package com.example.redisson_sample.service;

import com.example.redisson_sample.domain.repository.InventoryCheckRepository;
import com.example.redisson_sample.dto.ReqStartInventoryCheckDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class InventoryServiceTest {

    @Autowired
    InventoryCheckService inventoryCheckService;

    @Autowired
    InventoryCheckRepository inventoryCheckRepository;

    @Test
    @DisplayName("재고조사 테스트 - 분산락 미적용")
    void startInventoryCheck() throws InterruptedException {
        int numOfThread = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch latch = new CountDownLatch(numOfThread);
        ReqStartInventoryCheckDTO reqDTO = new ReqStartInventoryCheckDTO();
        reqDTO.setTeamId(1L);
        for (int i = 0; i < numOfThread; i++) {
            try {
                executorService.submit(() -> inventoryCheckService.start(reqDTO));
            } finally {
                latch.countDown();
            }
        }
        latch.await();
        Thread.sleep(5000); // 비동기로 실행되는 메서드가 온전히 종료되도록 대기
    }

    @Test
    @DisplayName("재고조사 테스트 - 분산락 적용")
    void startInventoryCheckWithLock() throws InterruptedException {
        int numOfThread = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch latch = new CountDownLatch(numOfThread);
        ReqStartInventoryCheckDTO reqDTO = new ReqStartInventoryCheckDTO();
        reqDTO.setTeamId(1L);
        for (int i = 0; i < numOfThread; i++) {
            try {
                executorService.submit(() -> inventoryCheckService.startWithLock("inventory-check-test", reqDTO));
            } finally {
                latch.countDown();
            }
        }
        latch.await();
        Thread.sleep(5000);        // 비동기로 실행되는 메서드가 온전히 종료되도록 대기
    }
}