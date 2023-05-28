package com.bruce.coupon.customer.producer;

import com.alibaba.fastjson.JSONObject;
import com.bruce.coupon.customer.constant.EventConstant;
import com.bruce.coupon.customer.domain.RequestCoupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Component
@RefreshScope
@Slf4j
public class CouponProducer {

    @Autowired
    private StreamBridge streamBridge ;

    @Value("${delayConsumSenconds:3}")
    private Integer delayConsumSenconds ;

    public Boolean sendCoupon(RequestCoupon requestCoupon){
        log.info("sendCoupon requestCoupon = {}" , JSONObject.toJSONString(requestCoupon));
        streamBridge.send(EventConstant.ADD_COUPON_EVENT,requestCoupon) ;
        return Boolean.TRUE ;
    }

    public Boolean sendDelayCoupon(RequestCoupon requestCoupon){
        log.info("sendDelayCoupon..delayConsumSenconds = [{}]... requestCoupon = {}",delayConsumSenconds ,JSONObject.toJSONString(requestCoupon));
        streamBridge.send(EventConstant.ADD_COUPON_DELAY_EVENT,
                MessageBuilder.withPayload(requestCoupon).setHeader(EventConstant.DELAY_PARAM,delayConsumSenconds * 1000).build()) ;
        return Boolean.TRUE ;
    }


    public Boolean deleteCoupon(Long userId, Long couponId) {
        log.info("sent delete coupon event: userId={}, couponId={}", userId, couponId);
        streamBridge.send(EventConstant.DELETE_COUPON_EVENT, userId + "," + couponId);
        return Boolean.TRUE;
    }
}
