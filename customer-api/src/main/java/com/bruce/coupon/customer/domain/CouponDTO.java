package com.bruce.coupon.customer.domain;

import com.bruce.coupon.customer.enums.CouponStatus;
import com.bruce.coupon.template.domain.CouponTemplateInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private Long id;

    // 对应的模板ID - 不使用one to one映射
    // 不推荐使用级联查询的原因是为了防止滥用而导致的DB性能问题
    private Long templateId;

    // 所有者的用户ID
    private Long userId;

    // 冗余一个shop id方便查找
    private Long shopId;

    // 优惠券的使用/未使用状态
    private CouponStatus status;

    // 被Transient标记的属性是不会被持久化的
    private CouponTemplateInfo templateInfo;

    // 获取时间自动生成
    private Date createdTime;
}
