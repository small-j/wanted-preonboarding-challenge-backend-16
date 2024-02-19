package com.wanted.preonboarding.ticket.presentation;

import com.wanted.preonboarding.core.domain.response.ResponseHandler;
import com.wanted.preonboarding.ticket.application.TicketSeller;
import com.wanted.preonboarding.ticket.domain.dto.ReservationResult;
import com.wanted.preonboarding.ticket.domain.dto.ReserveInfo;
import com.wanted.preonboarding.ticket.domain.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {
    private final TicketSeller ticketSeller;

    @PostMapping("/")
    public ResponseEntity<ResponseHandler<ReservationResult>> reservation(@RequestBody ReserveInfo reserveInfo) {
        return ResponseEntity
                .ok()
                .body(ResponseHandler.<ReservationResult>builder()
                    .message("Success")
                    .statusCode(HttpStatus.OK)
                    .data(ticketSeller.reserve(reserveInfo))
                    .build()
                );
    }

    @GetMapping()
    public ResponseEntity<ResponseHandler<List<ReservationResult>>> findReservation(@RequestParam UserInfo userInfo) {
        return ResponseEntity
                .ok()
                .body(ResponseHandler.<List<ReservationResult>>builder()
                        .message("Success")
                        .statusCode(HttpStatus.OK)
                        .data(ticketSeller.findReservationHistory(userInfo))
                        .build()
                );
    }

    @DeleteMapping("/cancel/{reservation_id}")
    public ResponseEntity cancelReservation(@PathVariable int reservationId) {
        ticketSeller.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }
}
