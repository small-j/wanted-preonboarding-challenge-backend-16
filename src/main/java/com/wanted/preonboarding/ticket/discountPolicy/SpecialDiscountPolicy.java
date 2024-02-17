package com.wanted.preonboarding.ticket.discountPolicy;

public class SpecialDiscountPolicy implements DiscountPolicy {

    @Override
    public Long discount(Long price) {
        return price - (price * 10 / 100);
    }
}
