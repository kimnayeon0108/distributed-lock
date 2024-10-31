package com.example.redisson_sample.domain.repository;

import com.example.redisson_sample.domain.entity.InventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long> {

    boolean existsByTeamId(Long teamId);
}
