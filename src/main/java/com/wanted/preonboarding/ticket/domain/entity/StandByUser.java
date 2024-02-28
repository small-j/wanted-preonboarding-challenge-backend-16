package com.wanted.preonboarding.ticket.domain.entity;

import com.wanted.preonboarding.ticket.domain.dto.StandByUserInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;
    private String name;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String email;

    public static StandByUser of (StandByUserInfo standByUserInfo, Performance performance) {
        return StandByUser.builder()
                .performance(performance)
                .name(standByUserInfo.getName())
                .phoneNumber(standByUserInfo.getPhoneNumber())
                .email(standByUserInfo.getEmail())
                .build();
    }
}
