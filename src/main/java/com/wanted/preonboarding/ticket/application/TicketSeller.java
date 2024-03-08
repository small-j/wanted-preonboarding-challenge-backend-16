package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.discountPolicy.DiscountPolicy;
import com.wanted.preonboarding.ticket.domain.ReservationStatus;
import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
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
    private final EmailSender emailSender;


    @Transactional
    public ReservationResult reserve(ReserveInfo reserveInfo) {
        Performance performance = performanceRepository.findById(reserveInfo.getPerformanceId())
                .orElseThrow(EntityNotFoundException::new);
        PerformanceSeatInfo performanceSeatInfo = performanceSeatInfoRepository.findPerformanceSeatInfo(performance, reserveInfo.getRound(), reserveInfo.getLine(), reserveInfo.getSeat());

        performanceSeatInfo.isEnabled(performance);

        pay(reserveInfo, getDiscountedPrice(performance.getPrice()));

        Reservation reservation = reservationRepository.save(Reservation.of(reserveInfo));
        performanceSeatInfo.setIsReserve(PerformanceSeatReserveStatus.disable);

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
        reservation.setIsCanceled(ReservationStatus.canceled);

        // smallj : 좌석 정보 업데이트 필요. -> 좌석 정보 업데이트를 위해 매번 공연을 조회해야하나?
        Performance performance = performanceRepository.findById(reservation.getPerformanceId())
                .orElseThrow(IllegalArgumentException::new);
        PerformanceSeatInfo performanceSeatInfo = performanceSeatInfoRepository.findPerformanceSeatInfo(performance, reservation.getRound(), reservation.getLine(), reservation.getSeat());

        performanceSeatInfo.setIsReserve(PerformanceSeatReserveStatus.enable);

        sendEmailToStandByUser(reservation);
    }

    @Transactional
    public StandByUserInfo addStandByUser(StandByUserInfo standByUserInfo) {
        Performance performance = performanceRepository.getReferenceById(standByUserInfo.getPerformanceId());
        StandByUser standByUser = StandByUser.of(standByUserInfo, performance);
        standByUserRepository.save(standByUser);
        // smallj : 이미 신청한 공연 대기라면 예외 처리 구현 필요.
        return StandByUserInfo.of(standByUser);
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

    private void sendEmailToStandByUser(Reservation reservation) {
        Performance performance = performanceRepository.findById(reservation.getPerformanceId())
                .orElseThrow(EntityNotFoundException::new);

        String title = "공연 예약 알림";
        String msg = performance.getName() + " 공연이 예약 가능합니다.";
        List<StandByUser> users = standByUserRepository.findByPerformanceId(reservation.getPerformanceId());
        users.stream()
                .forEach(standByUser -> emailSender.sendEmail(MailContent.of(standByUser.getEmail(), title, msg)));
    }
}
