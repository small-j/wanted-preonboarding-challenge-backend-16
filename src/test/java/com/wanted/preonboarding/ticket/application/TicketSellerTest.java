package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
import com.wanted.preonboarding.ticket.domain.ReservationStatus;
import com.wanted.preonboarding.ticket.domain.dto.*;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import com.wanted.preonboarding.ticket.domain.entity.Reservation;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceSeatInfoRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.ReservationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class TicketSellerTest {
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private PerformanceSeatInfoRepository performanceSeatInfoRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TicketSeller ticketSeller;


    @Test
    public void reserve() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        Performance savedPerformance = performanceRepository.save(performance);
        PerformanceSeatInfo performanceSeatInfo1 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();

        PerformanceSeatInfo performanceSeatInfo2 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(2)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();

        PerformanceSeatInfo performanceSeatInfo3 = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(3)
                .isReserve(PerformanceSeatReserveStatus.enable)
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

    @Test
    public void findReservationHistory() {
        // given
        Performance performance1 = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        performanceRepository.save(performance1);
        Performance performance2 = Performance.builder()
                .name("캣츠")
                .price(100000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        performanceRepository.save(performance2);

        PerformanceSeatInfo performanceSeatInfo1 = PerformanceSeatInfo.builder()
                .performance(performance1)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();
        performanceSeatInfoRepository.save(performanceSeatInfo1);
        PerformanceSeatInfo performanceSeatInfo2 = PerformanceSeatInfo.builder()
                .performance(performance2)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();
        performanceSeatInfoRepository.save(performanceSeatInfo2);

        ReservationResult reservation1 = ticketSeller.reserve(ReserveInfo.builder()
                .performanceId(performance1.getId())
                .reservationName("유진호")
                .reservationPhoneNumber("010-1234-1234")
                .reservationStatus("reserve")
                .amount(200000)
                .round(1)
                .line('A')
                .seat(1)
                .build()
        );
        ReservationResult reservation2 = ticketSeller.reserve(ReserveInfo.builder()
                .performanceId(performance2.getId())
                .reservationName("유진호")
                .reservationPhoneNumber("010-1234-1234")
                .reservationStatus("reserve")
                .amount(200000)
                .round(1)
                .line('A')
                .seat(1)
                .build()
        );

        // when
        List<ReservationResult> reservationResults = ticketSeller.findReservationHistory(new UserInfo("유진호", "010-1234-1234"));

        // then
        Assertions.assertThat(reservationResults.size()).isEqualTo(2);
    }

    @Test
    public void cancelReservation() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        PerformanceSeatInfo performanceSeatInfo = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();
        performanceRepository.save(performance);
        performanceSeatInfoRepository.save(performanceSeatInfo);

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

        // when
        ticketSeller.cancelReservation(result.getReservationId());

        // then
        Reservation reservation = reservationRepository.findById(result.getReservationId())
                .orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(reservation.getIsCanceled()).isEqualTo(ReservationStatus.canceled);
    }

    @Test
    void addStandByUser() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        performanceRepository.save(performance);
        StandByUserInfo standByUserInfo = StandByUserInfo.builder()
                .performanceId(performance.getId())
                .name("김지윤")
                .phoneNumber("010-1234-1234")
                .email("wldbs2043@naver.com")
                .build();

        // when
        StandByUserInfo result = ticketSeller.addStandByUser(standByUserInfo);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void sendEmailWhenCanceledReservation() {
        // given
        Performance performance = Performance.builder()
                .name("레베카")
                .price(10000)
                .round(1)
                .type(0)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .startDate(LocalDateTime.of(2024, 1, 14, 10, 34))
                .build();
        PerformanceSeatInfo performanceSeatInfo = PerformanceSeatInfo.builder()
                .performance(performance)
                .round(1)
                .gate(1)
                .line('A')
                .seat(1)
                .isReserve(PerformanceSeatReserveStatus.enable)
                .build();
        performanceRepository.save(performance);
        performanceSeatInfoRepository.save(performanceSeatInfo);

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

        StandByUserInfo standByUserInfo = StandByUserInfo.builder()
                .performanceId(performance.getId())
                .name("김지윤")
                .phoneNumber("010-1234-1234")
                .email("wldbs2043@naver.com")
                .build();
        ticketSeller.addStandByUser(standByUserInfo);


        // when
        ticketSeller.cancelReservation(result.getReservationId());

        // then
        // smallj: 이메일이 제대로 전달되었음을 알 수 있는 방법이 없을까?
    }
}
