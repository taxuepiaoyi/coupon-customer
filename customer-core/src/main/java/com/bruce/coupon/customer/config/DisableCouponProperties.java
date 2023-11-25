package com.bruce.coupon.customer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * nacos配置文件变更，亦可以使用ConfigurationProperties，实现不用重启服务，也能读取到变更后的内容
 */
@Data
@Component
@ConfigurationProperties
public class DisableCouponProperties {
    private Boolean disableCouponRequest ;
}
