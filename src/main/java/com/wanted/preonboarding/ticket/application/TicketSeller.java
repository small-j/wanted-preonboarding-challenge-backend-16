package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import com.wanted.preonboarding.ticket.domain.dto.ReservationResult;
import com.wanted.preonboarding.ticket.domain.dto.ReserveInfo;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import com.wanted.preonboarding.ticket.domain.entity.Reservation;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceSeatInfoRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketSeller {
    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatInfoRepository performanceSeatInfoRepository;
    private final ReservationRepository reservationRepository;

    private final String isEnable = "enable";
    private final String isDisable = "disable";

    public List<PerformanceInfo> getAllPerformanceInfoList() {
        return performanceRepository.findByIsReserve(isEnable)
            .stream()
            .map(PerformanceInfo::of)
            .toList();
    }

    public PerformanceInfo getPerformanceInfoDetail(String id) {
        return PerformanceInfo.of(performanceRepository.findById(UUID.fromString(id))
                .orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public ReservationResult reserve(ReserveInfo reserveInfo) {
        Performance performance = performanceRepository.findById(reserveInfo.getPerformanceId())
                .orElseThrow(EntityNotFoundException::new);
        PerformanceSeatInfo performanceSeatInfo = performanceSeatInfoRepository.findPerformanceSeatInfo(performance, reserveInfo.getRound(), reserveInfo.getLine(), reserveInfo.getSeat());

        isEnabled(performance, performanceSeatInfo);

        pay(reserveInfo, getPrice(performance.getPrice()));

        reservationRepository.save(Reservation.of(reserveInfo));
        performanceSeatInfo.setIsReserve(isDisable);

        return ReservationResult.of(performance, performanceSeatInfo, reserveInfo.getReservationName(), reserveInfo.getReservationPhoneNumber());
    }
    private void isEnabled(Performance performance, PerformanceSeatInfo performanceSeatInfo) {
        String result = performance.getIsReserve().equalsIgnoreCase(isEnable)
                && performanceSeatInfo.getIsReserve().equalsIgnoreCase(isEnable) ? isEnable : isDisable;

        String disableReservationMessage = "예약할 수 없는 공연(공연좌석) 입니다.";
        if (result.equalsIgnoreCase(isDisable))
            throw new IllegalArgumentException(disableReservationMessage);
    }

    private int getPrice(int price) {
        // smallj : discount policy를 적용한 금액 반환.
        return price;
    }

    private void pay(ReserveInfo reserveInfo, int price) {
        String amountNotEnoughMessage = "통장 잔고 부족으로 결제를 수행할 수 있는 상태가 아닙니다.";
        if (!isAmountEnough(reserveInfo.getAmount(), price))
            throw new IllegalStateException(amountNotEnoughMessage);

        reserveInfo.setAmount(reserveInfo.getAmount() - (long) price);
    }

    private boolean isAmountEnough(long amount, int price) {
        return amount - (long) price >= 0; // smallj :  magic number 처리 필요
    }
}
