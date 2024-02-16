package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import com.wanted.preonboarding.ticket.domain.dto.ReservationResult;
import com.wanted.preonboarding.ticket.domain.dto.ReserveInfo;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceSeatInfoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TicketSellerTest {
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private PerformanceSeatInfoRepository performanceSeatInfoRepository;

    @Autowired
    private TicketSeller ticketSeller;

    @Test
    public void getAllPerformanceInfoList() {
        // given
        Performance performance1 = Performance.builder()
                .name("레베카")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve("enable")
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance performance2 = Performance.builder()
                .name("캣츠")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve("enable")
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance performance3 = Performance.builder()
                .name("레미제라블")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve("disable")
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance savedPerformance1 = performanceRepository.save(performance1);
        Performance savedPerformance2 = performanceRepository.save(performance2);
        Performance savedPerformance3 = performanceRepository.save(performance3);

        // when
        List<PerformanceInfo> performanceInfoList = ticketSeller.getAllPerformanceInfoList();
        List<UUID> performanceIdList = performanceInfoList
                .stream()
                .map(performanceInfo -> performanceInfo.getPerformanceId())
                .toList();

        // then
        Assertions.assertThat(performanceIdList).contains(savedPerformance1.getId());
        Assertions.assertThat(performanceIdList).isNotIn(savedPerformance3.getId());
    }

    @Test
    public void reserve() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve("enable")
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance savedPerformance = performanceRepository.save(performance);
        PerformanceSeatInfo performanceSeatInfo1 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve("enable")
                .build();

        PerformanceSeatInfo performanceSeatInfo2 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(2)
                .isReserve("enable")
                .build();

        PerformanceSeatInfo performanceSeatInfo3 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(3)
                .isReserve("enable")
                .build();
        performanceSeatInfoRepository.save(performanceSeatInfo1);
        performanceSeatInfoRepository.save(performanceSeatInfo2);
        performanceSeatInfoRepository.save(performanceSeatInfo3);

        // when
        ReservationResult result = ticketSeller.reserve(ReserveInfo.builder()
                .performanceId(performance.getId())
                .reservationName("유진호")
                .reservationPhoneNumber("010-1234-1234")
                .reservationStatus("reserve")
                .amount(200000)
                .round(1)
                .line('A')
                .seat(1)
                .build()
        );

        // then
        Assertions.assertThat(result).isNotNull();
    }
}
