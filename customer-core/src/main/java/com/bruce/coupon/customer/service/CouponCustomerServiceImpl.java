package com.bruce.coupon.customer.service;

import com.bruce.coupon.calculation.domain.ShoppingCart;
import com.bruce.coupon.calculation.domain.SimulationOrder;
import com.bruce.coupon.calculation.domain.SimulationResponse;
import com.bruce.coupon.customer.converter.CouponConverter;
import com.bruce.coupon.customer.dao.CouponDao;
import com.bruce.coupon.customer.domain.CouponDTO;
import com.bruce.coupon.customer.domain.RequestCoupon;
import com.bruce.coupon.customer.domain.SearchCoupon;
import com.bruce.coupon.customer.entity.Coupon;
import com.bruce.coupon.customer.enums.CouponStatus;
import com.bruce.coupon.customer.feign.CalculationService;
import com.bruce.coupon.customer.feign.TemplateService;
import com.bruce.coupon.template.domain.CouponInfo;
import com.bruce.coupon.template.domain.CouponTemplateInfo;
import com.bruce.coupon.template.rule.TemplateRule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RefreshScope
@Service
@Transactional
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private TemplateService templateService ;

    @Autowired
    private CalculationService calculationService ;

    @Value("${seataExceptionFlag:false}")
    private Boolean seataExceptionFlag ;



    @Override
    public CouponDTO requestCoupon(RequestCoupon request) {
        CouponTemplateInfo templateInfo =  templateService.getTemplate(request.getCouponTemplateId()) ;

        // 模板不存在则报错
        if (templateInfo == null) {
            log.error("invalid template id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }

        // 模板不存在则报错
        TemplateRule templateRule = templateInfo.getRule() ;
        if (templateRule == null) {
            log.error("invalid template rule id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template rule id");
        }

        // 模板不能过期
        long now = Calendar.getInstance().getTimeInMillis();
        Long expTime = templateRule.getDeadline();
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
                .createdTime(new Date())
                .build();
        couponDao.save(coupon);
        CouponDTO couponDTO = CouponDTO.builder().build() ;
        BeanUtils.copyProperties(coupon,couponDTO);
         return couponDTO ;
    }

    @Override
    @Transactional
    public void deleteCoupon(Long userId, Long couponId) {
       templateService.deleteCouponTemplate(couponId) ;
       couponDao.deleteById(userId);
    }

    @Override
    @Transactional
    public void deleteCouponByCouponId(Long couponId) {
        templateService.deleteCouponTemplate(couponId) ;
        couponDao.deleteCouponInBatch(couponId,CouponStatus.INACTIVE);
        if(seataExceptionFlag){
            throw new RuntimeException("deleteCouponByCouponId  exception...") ;
        }
    }

    @Override
    @Transactional
    public ShoppingCart placeOrder(ShoppingCart order) {
        if (CollectionUtils.isEmpty(order.getProducts())) {
            log.error("invalid check out request, order={}", order);
            throw new IllegalArgumentException("cart if empty");
        }

        Coupon coupon = null;
        if (order.getCouponId() != null) {
            // 如果有优惠券，验证是否可用，并且是当前客户的
            Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(order.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();
            coupon = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst()
                    // 如果找不到券，就抛出异常
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);
            couponInfo.setTemplate(templateService.getTemplate(couponInfo.getTemplateId()));

            order.setCouponInfos(Lists.newArrayList(couponInfo));
        }

        // order清算
        ShoppingCart checkoutInfo = calculationService.checkout(order);

        if (coupon != null) {
            // 如果优惠券没有被结算掉，而用户传递了优惠券，报错提示该订单满足不了优惠条件
            if (CollectionUtils.isEmpty(checkoutInfo.getCouponInfos())) {
                log.error("cannot apply coupon to order, couponId={}", coupon.getId());
                throw new IllegalArgumentException("coupon is not applicable to this order");
            }

            log.info("update coupon status to used, couponId={}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
            couponDao.save(coupon);
        }

        return checkoutInfo;
    }

    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {
        List<CouponInfo> couponInfos = Lists.newArrayList();
        // 挨个循环，把优惠券信息加载出来
        // 高并发场景下不能这么一个个循环，更好的做法是批量查询
        // 而且券模板一旦创建不会改内容，所以在创建端做数据异构放到缓存里，使用端从缓存捞template信息
        for (Long couponId : order.getCouponIDs()) {
            Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(couponId)
                    .status(CouponStatus.AVAILABLE)
                    .build();
            Optional<Coupon> couponOptional = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst();
            // 加载优惠券模板信息
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);

                CouponTemplateInfo templateInfo = templateService.getTemplate(couponInfo.getTemplateId());

                couponInfo.setTemplate(templateInfo);
                couponInfos.add(couponInfo);
            }
        }
        order.setCouponInfos(couponInfos);

        return calculationService.simulate(order);
    }

    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        return null;
    }
}
