package com.bruce.coupon.customer.service;

import com.bruce.coupon.customer.domain.CouponDTO;
import com.bruce.coupon.customer.domain.RequestCoupon;
import com.bruce.coupon.customer.domain.SearchCoupon;
import com.bruce.coupon.template.domain.CouponInfo;

import java.util.List;

public interface CouponCustomerService {
    // 领券接口
    CouponDTO requestCoupon(RequestCoupon request);

    void deleteCoupon(Long userId, Long couponId);

    // 查询用户优惠券
    List<CouponInfo> findCoupon(SearchCoupon request);
}
