package com.example.redisson_sample.controller;

import com.example.redisson_sample.dto.ReqStartInventoryCheckDTO;
import com.example.redisson_sample.service.InventoryCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LockController {

    private final InventoryCheckService inventoryCheckService;

    @PostMapping("/inventory-checks")
    public void issueCoupon(@RequestBody ReqStartInventoryCheckDTO reqDTO) {
        inventoryCheckService.startWithLock("inventory-check", reqDTO);
    }
}
