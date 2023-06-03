package com.bruce.coupon.customer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.bruce.coupon.calculation.domain.ShoppingCart;
import com.bruce.coupon.calculation.domain.SimulationOrder;
import com.bruce.coupon.calculation.domain.SimulationResponse;
import com.bruce.coupon.customer.domain.CouponDTO;
import com.bruce.coupon.customer.domain.RequestCoupon;
import com.bruce.coupon.customer.domain.SearchCoupon;
import com.bruce.coupon.customer.producer.CouponProducer;
import com.bruce.coupon.customer.service.CouponCustomerService;
import com.bruce.coupon.template.domain.CouponInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RefreshScope
@RequestMapping("coupon-customer")
public class CouponCustomerController {

    @Value("${disableCouponRequest:false}")
    private Boolean disableCoupon;

    @Autowired
    private CouponCustomerService customerService;

    @Autowired
    private CouponProducer couponProducer ;

    @PostMapping("requestCoupon")
    @SentinelResource(value =  "requestCoupon")
    @ResponseBody
    public CouponDTO requestCoupon(@Valid @RequestBody RequestCoupon request) {
        if(disableCoupon){
            log.info("暂停优惠券活动");
            return CouponDTO.builder().build() ;
        }
        return customerService.requestCoupon(request);
    }

    // 用户删除优惠券
    @DeleteMapping("deleteCouponByCouponId")
    @GlobalTransactional(name = "coupon-customer", rollbackFor = Exception.class)
    public void deleteCouponByCouponId(@RequestParam("couponId") Long couponId) {
        customerService.deleteCouponByCouponId(couponId);
    }

    // 用户模拟计算每个优惠券的优惠价格
    @PostMapping("simulateOrder")
    public SimulationResponse simulate(@Valid @RequestBody SimulationOrder order) {
        return customerService.simulateOrderPrice(order);
    }

    // ResponseEntity - 指定返回状态码 - 可以作为一个课后思考题
    @PostMapping("placeOrder")
    public ShoppingCart checkout(@Valid @RequestBody ShoppingCart info) {
        return customerService.placeOrder(info);
    }


    // 实现的时候最好封装一个search object类
    @PostMapping("findCoupon")
    @SentinelResource(value = "findCoupon")
    public List<CouponInfo> findCoupon(@Valid @RequestBody SearchCoupon request) {
        return customerService.findCoupon(request);
    }

    /**
     * 用户创建优惠券
     * @param request
     * @return
     */
    @PostMapping("requestCouponEvent")
    public Boolean requestCouponEvent(@Valid @RequestBody RequestCoupon request) {
        return couponProducer.sendCoupon(request);
    }


    /**
     * 延时创建优惠券
     * @param request
     * @return
     */
    @PostMapping("requestDelayCouponEvent")
    public Boolean requestDelayCouponEvent(@Valid @RequestBody RequestCoupon request) {
        return couponProducer.sendDelayCoupon(request);
    }

    // 用户删除优惠券
    @DeleteMapping("deleteCouponEvent")
    public Boolean deleteCouponEvent(@RequestParam("userId") Long userId,
                                  @RequestParam("couponId") Long couponId) {
       return  couponProducer.deleteCoupon(userId, couponId);
    }

}

