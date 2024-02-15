package com.wanted.preonboarding.ticket.presentation;

import com.wanted.preonboarding.core.domain.response.ResponseHandler;
import com.wanted.preonboarding.ticket.application.TicketSeller;
import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("query")
@RequiredArgsConstructor
public class QueryController {
    private final TicketSeller ticketSeller;

    @GetMapping("/all/performance")
    public ResponseEntity<ResponseHandler<List<PerformanceInfo>>> getAllPerformanceInfoList() {
        return ResponseEntity
                .ok()
                .body(ResponseHandler.<List<PerformanceInfo>>builder()
                        .message("Success")
                        .statusCode(HttpStatus.OK)
                        .data(ticketSeller.getAllPerformanceInfoList())
                        .build()
                );
    }

    @GetMapping("/performance/{performance_id}")
    public ResponseEntity<ResponseHandler<PerformanceInfo>> getPerformanceInfo(@PathVariable String performanceId) {
        return ResponseEntity
                .ok()
                .body(ResponseHandler.<PerformanceInfo>builder()
                        .message("Success")
                        .statusCode(HttpStatus.OK)
                        .data(ticketSeller.getPerformanceInfoDetail(performanceId))
                        .build()
                );
    }
}
