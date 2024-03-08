package com.wanted.preonboarding.ticket.domain.entity;

import com.wanted.preonboarding.ticket.domain.PerformanceSeatReserveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "performance_seat_info")
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSeatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;
    @Column(nullable = false)
    private int round;
    @Column(nullable = false)
    private int gate;
    @Column(nullable = false)
    private char line;
    @Column(nullable = false)
    private int seat;
    @Column(nullable = false, columnDefinition = "varchar default 'enable'")
    @Enumerated(EnumType.STRING)
    private PerformanceSeatReserveStatus isReserve;

    public void setIsReserve(PerformanceSeatReserveStatus status) {
        this.isReserve = status;
    }

    public void isEnabled(Performance performance) {
        PerformanceSeatReserveStatus result =
                performance.getIsReserve() == PerformanceSeatReserveStatus.enable
                && this.getIsReserve() == PerformanceSeatReserveStatus.enable
                ? PerformanceSeatReserveStatus.enable : PerformanceSeatReserveStatus.disable;

        String disableReservationMessage = "예약할 수 없는 공연(공연좌석) 입니다.";
        if (result == PerformanceSeatReserveStatus.disable)
            throw new IllegalArgumentException(disableReservationMessage);
    }
}
