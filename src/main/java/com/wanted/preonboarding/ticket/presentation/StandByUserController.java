package com.wanted.preonboarding.ticket.presentation;

import com.wanted.preonboarding.core.domain.response.ResponseHandler;
import com.wanted.preonboarding.ticket.application.TicketSeller;
import com.wanted.preonboarding.ticket.domain.dto.StandByUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stand")
@RequiredArgsConstructor
public class StandByUserController {
    private final TicketSeller ticketSeller;

    @PostMapping("")
    public ResponseEntity<ResponseHandler<StandByUserInfo>> addStandByUser(@RequestBody StandByUserInfo standByUserInfo) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<StandByUserInfo>builder()
                    .message("Success")
                    .statusCode(HttpStatus.OK)
                    .data(ticketSeller.addStandByUser(standByUserInfo))
                    .build()
                );
    }
}
