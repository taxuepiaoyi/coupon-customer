package com.bruce.coupon.customer.feign.fallback;

import com.bruce.coupon.customer.feign.TemplateService;
import com.bruce.coupon.template.domain.CouponTemplateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class TemplateServiceFallback implements TemplateService {
    @Override
    public CouponTemplateInfo getTemplate(Long id) {
        log.info("TemplateServiceFallback  getTemplate....id:" + id + "......");
        return null;
    }

    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(Collection<Long> ids) {
        log.info("TemplateServiceFallback  getTemplateInBatch.....ids:{}........",ids);
        return null;
    }

    @Override
    public Boolean deleteCouponTemplate(Long templateId) {
        log.info("TemplateServiceFallback  deleteCouponTemplate.....templateId:{}........",templateId);
        return Boolean.FALSE;
    }
}
