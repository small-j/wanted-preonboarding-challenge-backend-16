package com.wanted.preonboarding.ticket.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
@Entity
public class PerformanceSeatInfo {
    @Id
    @GeneratedValue
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
}
