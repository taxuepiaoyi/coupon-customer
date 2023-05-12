package com.bruce.coupon.customer.feign.fallback;

import com.bruce.coupon.calculation.domain.ShoppingCart;
import com.bruce.coupon.calculation.domain.SimulationOrder;
import com.bruce.coupon.calculation.domain.SimulationResponse;
import com.bruce.coupon.customer.feign.CalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CalculationServiceFallbackFactory implements FallbackFactory<CalculationService> {
    @Override
    public CalculationService create(Throwable cause) {
        return new CalculationService() {
            @Override
            public ShoppingCart checkout(ShoppingCart settlement) {
                log.info("CalculationServiceFallbackFactory......checkout.....test");
                return null;
            }

            @Override
            public SimulationResponse simulate(SimulationOrder simulator) {
                log.info("CalculationServiceFallbackFactory......simulate...test...");
                return null;
            }
        };
    }
}
