package com.wanted.preonboarding.ticket.infrastructure.repository;

import com.wanted.preonboarding.ticket.domain.entity.StandByUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandByUserRepository extends JpaRepository<StandByUser, Integer> {
}
