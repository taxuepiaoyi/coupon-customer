package com.bruce.coupon.customer.feign;

import com.bruce.coupon.calculation.domain.ShoppingCart;
import com.bruce.coupon.calculation.domain.SimulationOrder;
import com.bruce.coupon.calculation.domain.SimulationResponse;
import com.bruce.coupon.customer.feign.fallback.CalculationServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "coupon-calculation" ,path = "/calculate" ,fallbackFactory = CalculationServiceFallbackFactory.class)
public interface CalculationService {

    // 优惠券结算
    @PostMapping("/checkout")
    ShoppingCart checkout(ShoppingCart settlement);

    // 优惠券列表挨个试算
    // 给客户提示每个可用券的优惠额度，帮助挑选
    @PostMapping("/simulate")
    SimulationResponse simulate(SimulationOrder simulator);
}
