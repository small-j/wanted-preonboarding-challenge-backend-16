package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.ticket.domain.dto.ReservationResult;
import com.wanted.preonboarding.observer.IObserver;

public class TicketCancelObserver implements IObserver<ReservationResult> {


    @Override
    public boolean update(ReservationResult reservationResult) {
        return false;
    }
}
