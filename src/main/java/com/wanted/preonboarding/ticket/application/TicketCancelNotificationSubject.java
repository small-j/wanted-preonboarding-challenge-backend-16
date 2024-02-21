package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.observer.IObserver;
import com.wanted.preonboarding.observer.ISubject;
import com.wanted.preonboarding.ticket.domain.dto.ReservationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TicketCancelNotificationSubject implements ISubject<ReservationResult> {
    private final Set<IObserver> observers;

    @Override
    public boolean register(IObserver newObserver) {
        return observers.add(newObserver);
    }

    @Override
    public boolean remove(IObserver observer) {
        return observers.remove(observer);
    }

    @Override
    public boolean sendMessage(ReservationResult reservationResult) {
        observers.stream()
                .forEach(observer -> observer.update(reservationResult));

        // smallj : 무조건 true를 반환할게 아니라 forEach 구문 안에서 에러가 발생했을 때 어떻게 처리할지 고민해보기.
        return true;
    }
}
