package com.bruce.coupon.customer.feign;

import com.bruce.coupon.customer.feign.fallback.TemplateServiceFallback;
import com.bruce.coupon.template.domain.CouponTemplateInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

@FeignClient(value = "coupon-template" ,path = "/template", fallback = TemplateServiceFallback.class)
public interface TemplateService {

    // 读取优惠券
    @GetMapping("/getTemplate")
    CouponTemplateInfo getTemplate(@RequestParam("id") Long id);

    // 批量获取
    @GetMapping("/getBatch")
    Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids);

    //删除优惠券模板
    @DeleteMapping("/deleteCouponTemplate")
    Boolean deleteCouponTemplate(@RequestParam("templateId") Long templateId) ;

    //删除优惠券模板
    @DeleteMapping("/deleteCouponTemplateTCC")
    Boolean deleteCouponTemplateTCC(@RequestParam("templateId") Long templateId) ;

}
