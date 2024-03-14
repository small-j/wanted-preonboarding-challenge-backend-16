package com.wanted.preonboarding.ticket.presentation;

import com.wanted.preonboarding.core.domain.response.ResponseHandler;
import com.wanted.preonboarding.ticket.application.PerformanceMaintainer;
import com.wanted.preonboarding.ticket.domain.dto.PerformanceInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("query")
@RequiredArgsConstructor
public class QueryController {
    private final PerformanceMaintainer performanceMaintainer;

    @GetMapping("/all/performance")
    public ResponseEntity<ResponseHandler<List<PerformanceInfo>>> getAllPerformanceInfoList() {
        return ResponseEntity
                .ok()
                .body(ResponseHandler.<List<PerformanceInfo>>builder()
                        .message("Success")
                        .statusCode(HttpStatus.OK)
                        .data(performanceMaintainer.getAllPerformanceInfoList())
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
                        .data(performanceMaintainer.getPerformanceInfoDetail(performanceId))
                        .build()
                );
    }

    //=controller 예외처리=//
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
}
