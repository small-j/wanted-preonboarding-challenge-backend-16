package com.wanted.preonboarding.ticket.domain.dto;

import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootTest
public class PerformanceTests {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Test
    public void enumTest() {
        int code = 2;
        String typeName = Arrays.stream(PerformanceType.values()).filter(value -> value.getCategory() == code)
                .findFirst()
                .orElse(PerformanceType.NONE)
                .name();
        assertEquals("EXHIBITION", typeName);
    }

    @Test
    public void performanceSave() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();

        // when
        Performance savedPerformance = performanceRepository.save(performance);

        // then
        Performance findPerformance = performanceRepository.findById(savedPerformance.getId())
                .orElseThrow(EntityNotFoundException::new);
        Assertions.assertThat(findPerformance.getId()).isEqualTo(savedPerformance.getId());
    }
}
