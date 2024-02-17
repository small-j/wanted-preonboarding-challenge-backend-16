package com.wanted.preonboarding.ticket.config;

import com.wanted.preonboarding.ticket.discountPolicy.DiscountPolicy;
import com.wanted.preonboarding.ticket.discountPolicy.SpecialDiscountPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackendPreonboardingAppConfig {

    @Bean
    public DiscountPolicy discountPolicy() {
        return new SpecialDiscountPolicy();
    }
}
