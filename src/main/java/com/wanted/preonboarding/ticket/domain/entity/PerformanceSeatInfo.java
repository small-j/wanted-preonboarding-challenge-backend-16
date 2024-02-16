package com.wanted.preonboarding.ticket.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
    @Column(nullable = false, columnDefinition = "varchar default 'disable'")
    private String isReserve;

    public void setIsReserve(String isReserve) {
        this.isReserve = isReserve;
    }
}
