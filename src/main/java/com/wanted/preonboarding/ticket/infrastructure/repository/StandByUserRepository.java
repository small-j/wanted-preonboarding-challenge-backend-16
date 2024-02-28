package com.wanted.preonboarding.ticket.infrastructure.repository;

import com.wanted.preonboarding.ticket.domain.entity.StandByUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StandByUserRepository extends JpaRepository<StandByUser, Integer> {
    public List<StandByUser> findByPerformanceId(UUID id);
}
