package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PerformanceMaintainerTest {

    @Autowired
    private PerformanceMaintainer performanceMaintainer;
    @Autowired
    private PerformanceRepository performanceRepository;

    @Test
    void getPerformanceInfoDetail() {
    }

    @Test
    public void getAllPerformanceInfoList() {
        // given
        Performance performance1 = Performance.builder()
                .name("레베카")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance performance2 = Performance.builder()
                .name("캣츠")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance performance3 = Performance.builder()
                .name("레미제라블")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance savedPerformance1 = performanceRepository.save(performance1);
        Performance savedPerformance2 = performanceRepository.save(performance2);
        Performance savedPerformance3 = performanceRepository.save(performance3);

        // when
        List<PerformanceInfo> performanceInfoList = performanceMaintainer.getAllPerformanceInfoList();
        List<UUID> performanceIdList = performanceInfoList
                .stream()
                .map(performanceInfo -> performanceInfo.getPerformanceId())
                .toList();

        // then
        Assertions.assertThat(performanceIdList).contains(savedPerformance1.getId());
        Assertions.assertThat(performanceIdList).isNotIn(savedPerformance3.getId());
    }
}