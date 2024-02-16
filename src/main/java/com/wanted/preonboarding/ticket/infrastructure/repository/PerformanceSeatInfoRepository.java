package com.wanted.preonboarding.ticket.infrastructure.repository;

import com.wanted.preonboarding.ticket.domain.entity.Performance;
import com.wanted.preonboarding.ticket.domain.entity.PerformanceSeatInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;


@Repository
public class PerformanceSeatInfoRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(PerformanceSeatInfo performanceSeatInfo) {
        em.persist(performanceSeatInfo);
    }

    public PerformanceSeatInfo findPerformanceSeatInfo(Performance performance, int round, char line, int seat) {
        return (PerformanceSeatInfo) em.createQuery("select p from PerformanceSeatInfo as p where p.performance = :performance and p.round = :round and p.line = :line and p.seat = :seat")
                .setParameter("performance", performance)
                .setParameter("round", round)
                .setParameter("line", line)
                .setParameter("seat", seat)
                .getSingleResult();
    }
}
