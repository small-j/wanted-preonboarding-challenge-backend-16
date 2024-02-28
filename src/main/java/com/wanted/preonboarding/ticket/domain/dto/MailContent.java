package com.wanted.preonboarding.ticket.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailContent {
    private String email;
    private String title;
    private String message;

    public static MailContent of(String email, String title, String message) {
        return MailContent.builder()
                .email(email)
                .title(title)
                .message(message)
                .build();
    }
}
