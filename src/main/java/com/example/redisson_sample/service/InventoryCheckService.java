package com.example.redisson_sample.service;

import com.example.redisson_sample.annotation.DistributedLock;
import com.example.redisson_sample.domain.entity.InventoryCheck;
import com.example.redisson_sample.domain.repository.InventoryCheckRepository;
import com.example.redisson_sample.dto.ReqStartInventoryCheckDTO;
import com.example.redisson_sample.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryCheckService {

    private final InventoryCheckRepository inventoryCheckRepository;

    @DistributedLock(key = "#lockName.concat('-').concat(#reqDTO.getTeamId())", timeUnit = TimeUnit.SECONDS, waitTime = 3L, leaseTime = 1L)
    public void startWithLock(String lockName, ReqStartInventoryCheckDTO reqDTO) {
        boolean exists = inventoryCheckRepository.existsByTeamId(reqDTO.getTeamId());
        if (exists) {
            throw new CustomException("이미 재고 조사가 시작되었습니다.");
        }
        inventoryCheckRepository.save(new InventoryCheck(reqDTO.getTeamId()));
    }

    @Transactional
    public void start(ReqStartInventoryCheckDTO reqDTO) {
        boolean exists = inventoryCheckRepository.existsByTeamId(reqDTO.getTeamId());
        if (exists) {
            throw new CustomException("이미 재고 조사가 시작되었습니다.");
        }
        inventoryCheckRepository.save(new InventoryCheck(reqDTO.getTeamId()));
    }
}
