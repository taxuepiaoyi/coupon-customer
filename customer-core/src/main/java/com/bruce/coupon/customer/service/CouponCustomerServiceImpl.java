package com.bruce.coupon.customer.service;

import com.bruce.coupon.customer.constant.Constant;
import com.bruce.coupon.customer.dao.CouponDao;
import com.bruce.coupon.customer.domain.CouponDTO;
import com.bruce.coupon.customer.domain.RequestCoupon;
import com.bruce.coupon.customer.domain.SearchCoupon;
import com.bruce.coupon.customer.entity.Coupon;
import com.bruce.coupon.customer.enums.CouponStatus;
import com.bruce.coupon.customer.feign.CalculationService;
import com.bruce.coupon.template.domain.CouponInfo;
import com.bruce.coupon.template.domain.CouponTemplateInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private CalculationService calculationService ;


    @Override
    public CouponDTO requestCoupon(RequestCoupon request) {
        CouponTemplateInfo templateInfo = webClientBuilder.build()
                // 声明了这是一个GET方法
                .get()
                .uri("http://coupon-template-serv/template/getTemplate?id=" + request.getCouponTemplateId())
                .header(Constant.TRAFFIC_VERSION, request.getTrafficVersion())
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();

        // 模板不存在则报错
        if (templateInfo == null) {
            log.error("invalid template id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }

        // 模板不能过期
        long now = Calendar.getInstance().getTimeInMillis();
        Long expTime = templateInfo.getRule().getDeadline();
        if (expTime != null && now >= expTime || BooleanUtils.isFalse(templateInfo.getAvailable())) {
            log.error("template is not available id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("template is unavailable");
        }

        // 用户领券数量超过上限
        Long count = couponDao.countByUserIdAndTemplateId(request.getUserId(), request.getCouponTemplateId());
        if (count >= templateInfo.getRule().getLimitation()) {
            log.error("exceeds maximum number");
            throw new IllegalArgumentException("exceeds maximum number");
        }

        Coupon coupon = Coupon.builder()
                .templateId(request.getCouponTemplateId())
                .userId(request.getUserId())
                .shopId(templateInfo.getShopId())
                .status(CouponStatus.AVAILABLE)
                .templateInfo(templateInfo)
                .build();
        couponDao.save(coupon);
        CouponDTO couponDTO = CouponDTO.builder().build() ;
        BeanUtils.copyProperties(coupon,couponDTO);
         return couponDTO ;
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {

    }

    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        return null;
    }
}
