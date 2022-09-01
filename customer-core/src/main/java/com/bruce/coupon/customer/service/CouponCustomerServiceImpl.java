package com.bruce.coupon.customer.service;

import com.bruce.coupon.customer.domain.CouponDTO;
import com.bruce.coupon.customer.domain.RequestCoupon;
import com.bruce.coupon.customer.domain.SearchCoupon;
import com.bruce.coupon.template.domain.CouponInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class CouponCustomerServiceImpl implements CouponCustomerService {
    @Override
    public CouponDTO requestCoupon(RequestCoupon request) {
        return null;
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {

    }

    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        return null;
    }
}
