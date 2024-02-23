package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.discountPolicy.DiscountPolicy;
import com.wanted.preonboarding.ticket.domain.dto.*;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import com.wanted.preonboarding.ticket.domain.entity.Reservation;
import com.wanted.preonboarding.ticket.domain.entity.StandByUser;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceSeatInfoRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.ReservationRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.StandByUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketSeller {
    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatInfoRepository performanceSeatInfoRepository;
    private final ReservationRepository reservationRepository;
    private final StandByUserRepository standByUserRepository;
    private final DiscountPolicy discountPolicy;

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

        pay(reserveInfo, getDiscountedPrice(performance.getPrice()));

        Reservation reservation = reservationRepository.save(Reservation.of(reserveInfo));
        performanceSeatInfo.setIsReserve(isDisable);

        return ReservationResult.of(performance, reservation);
    }

    public List<ReservationResult> findReservationHistory(UserInfo userInfo) {
        List<Reservation> reservations = reservationRepository.findByNameAndPhoneNumber(userInfo.getName(), userInfo.getPhoneNumber());

        List<UUID> performanceIds = reservations.stream().map(reservation -> reservation.getPerformanceId()).toList();
        List<Performance> performances = performanceRepository.findDistinctByIdIn(performanceIds);

        List<ReservationResult> result = new ArrayList<>();
        for (Performance performance: performances) {
            List<Reservation> temp = reservations
                    .stream()
                    .filter(reservation -> reservation.getPerformanceId().toString().equals(performance.getId().toString()))
                    .toList();

                    temp.forEach(reservation -> result.add(ReservationResult.of(performance, reservation)));
        }

        return result;
    }

    @Transactional
    public void cancelReservation(int reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(IllegalArgumentException::new);
        reservation.setIsCanceled(isEnable);

        // smallj : 좌석 정보 업데이트 필요. -> 좌석 정보 업데이트를 위해 매번 공연을 조회해야하나?
        Performance performance = performanceRepository.findById(reservation.getPerformanceId())
                .orElseThrow(IllegalArgumentException::new);
        PerformanceSeatInfo performanceSeatInfo = performanceSeatInfoRepository.findPerformanceSeatInfo(performance, reservation.getRound(), reservation.getLine(), reservation.getSeat());

        performanceSeatInfo.setIsReserve(isEnable);
    }

    @Transactional
    public StandByUserInfo addStandByUser(StandByUserInfo standByUserInfo) {
        Performance performance = performanceRepository.getReferenceById(standByUserInfo.getPerformanceId());
        StandByUser standByUser = StandByUser.of(standByUserInfo, performance);
        standByUserRepository.save(standByUser);
        // smallj : 이미 신청한 공연 대기라면 예외 처리 구현 필요.
        return StandByUserInfo.of(standByUser);
    }

    private void isEnabled(Performance performance, PerformanceSeatInfo performanceSeatInfo) {
        String result = performance.getIsReserve().equalsIgnoreCase(isEnable)
                && performanceSeatInfo.getIsReserve().equalsIgnoreCase(isEnable) ? isEnable : isDisable;

        String disableReservationMessage = "예약할 수 없는 공연(공연좌석) 입니다.";
        if (result.equalsIgnoreCase(isDisable))
            throw new IllegalArgumentException(disableReservationMessage);
    }

    private long getDiscountedPrice(long price) {
        return discountPolicy.discount(price);
    }

    private void pay(ReserveInfo reserveInfo, long price) {
        String amountNotEnoughMessage = "통장 잔고 부족으로 결제를 수행할 수 있는 상태가 아닙니다.";
        if (!isAmountEnough(reserveInfo.getAmount(), price))
            throw new IllegalStateException(amountNotEnoughMessage);

        reserveInfo.setAmount(reserveInfo.getAmount() - price);
    }

    private boolean isAmountEnough(long amount, long price) {
        return amount - price >= 0; // smallj :  magic number 처리 필요
    }
}
