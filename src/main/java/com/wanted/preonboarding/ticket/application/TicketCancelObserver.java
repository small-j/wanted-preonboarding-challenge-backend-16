package com.wanted.preonboarding.ticket.application;

import com.wanted.preonboarding.observer.ISubject;
import com.wanted.preonboarding.observer.IObserver;
import com.wanted.preonboarding.ticket.domain.dto.MailContent;
import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.Reservation;
import com.wanted.preonboarding.ticket.domain.entity.StandByUser;
import com.wanted.preonboarding.ticket.infrastructure.repository.PerformanceRepository;
import com.wanted.preonboarding.ticket.infrastructure.repository.StandByUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TicketCancelObserver implements IObserver<Reservation> {
    private final ISubject subject;
    private final PerformanceRepository performanceRepository;
    private final StandByUserRepository standByUserRepository;
    private final EmailSender emailSender;

    @Autowired
    public TicketCancelObserver(ISubject subject, PerformanceRepository performanceRepository, StandByUserRepository standByUserRepository, EmailSender emailSender) {
        this.subject = subject;
        this.performanceRepository = performanceRepository;
        this.standByUserRepository = standByUserRepository;
        this.emailSender = emailSender;

        subject.register(this); // 자기자신이 subject에 자기자신을 등록하는것이 맞을까?
    }

    @Override
    public boolean update(Reservation reservation) {
        Performance performance = performanceRepository.findById(reservation.getPerformanceId())
                .orElseThrow(EntityNotFoundException::new);

        String title = "공연 예약 알림";
        String msg = performance.getName() + " 공연이 예약 가능합니다.";
        List<StandByUser> users = standByUserRepository.findByPerformanceId(reservation.getPerformanceId());
         users.stream()
                .forEach(standByUser -> emailSender.sendEmail(MailContent.of(standByUser.getEmail(), title, msg)));

        return true;
    }
}
