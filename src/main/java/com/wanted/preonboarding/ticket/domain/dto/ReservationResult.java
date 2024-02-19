package com.wanted.preonboarding.ticket.domain.dto;

import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import com.wanted.preonboarding.ticket.domain.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ReservationResult {
    private int reservationId;
    private UUID performanceId;
    private String performanceName;
    private String reservationName;
    private String reservationPhoneNumber;
    private int round;
    private char line;
    private int seat;

    public static ReservationResult of(Performance performance, Reservation reservation) {
        return ReservationResult.builder()
                .reservationId(reservation.getId())
                .performanceId(performance.getId())
                .performanceName(performance.getName())
                .reservationName(reservation.getName())
                .reservationPhoneNumber(reservation.getPhoneNumber())
                .round(reservation.getRound())
                .line(reservation.getLine())
                .seat(reservation.getSeat())
                .build();
    }
}
