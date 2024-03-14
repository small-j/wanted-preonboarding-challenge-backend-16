package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PerformanceMaintainer {

    private final PerformanceRepository performanceRepository;

    public List<PerformanceInfo> getAllPerformanceInfoList() {
        return performanceRepository.findByIsReserve(PerformanceSeatReserveStatus.enable)
                .stream()
                .map(PerformanceInfo::of)
                .toList();
    }

    public PerformanceInfo getPerformanceInfoDetail(String id) {
        return PerformanceInfo.of(performanceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공연입니다.")));
    }

}
