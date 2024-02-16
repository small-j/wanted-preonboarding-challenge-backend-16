package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
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

    private String isEnable = "enable";
    private String isDisable = "disable";

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
    public boolean reserve(ReserveInfo reserveInfo) {
        Performance performance = performanceRepository.findById(reserveInfo.getPerformanceId())
                .orElseThrow(EntityNotFoundException::new);
        PerformanceSeatInfo performanceSeatInfo = performanceSeatInfoRepository.findPerformanceSeatInfo(performance, reserveInfo.getRound(), reserveInfo.getLine(), reserveInfo.getSeat());

        if (isEnabled(performance, performanceSeatInfo)) {
            // 1. 결제
            pay(reserveInfo, getPrice(performance.getPrice()));

            // 2. 예매 진행
            // smallj : 공연 좌석 isReserve 정보 변경.
            reservationRepository.save(Reservation.of(reserveInfo));

            // smallj : 반환 정보 변경 필요.
            return true;

        } else {
            return false;
        }
    }
    private boolean isEnabled(Performance performance, PerformanceSeatInfo performanceSeatInfo) {
        String result = performance.getIsReserve().equalsIgnoreCase(isEnable)
                && performanceSeatInfo.getIsReserve().equalsIgnoreCase(isEnable) ? isEnable : isDisable;
        return result.equalsIgnoreCase(isEnable);
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
