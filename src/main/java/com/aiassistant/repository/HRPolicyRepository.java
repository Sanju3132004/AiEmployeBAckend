package com.aiassistant.repository;

import com.aiassistant.entity.HRPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HRPolicyRepository extends JpaRepository<HRPolicy, Long> {
    List<HRPolicy> findByCategoryIgnoreCase(String category);
}
