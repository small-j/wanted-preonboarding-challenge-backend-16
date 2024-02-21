package com.wanted.preonboarding.ticket.domain.dto;

import com.wanted.preonboarding.ticket.domain.entity.StandByUser;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class StandByUserInfo {
    private UUID performanceId;
    private String name;
    private String phoneNumber;
    private String email;

    public static StandByUserInfo of(StandByUser entity) {
        return StandByUserInfo.builder()
                .performanceId(entity.getPerformance().getId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .build();
    }
}
