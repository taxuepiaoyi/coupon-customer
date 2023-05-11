package com.bruce.coupon.customer;

import com.bruce.coupon.customer.loadbalance.CanaryRuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bruce"})
@EnableDiscoveryClient
@LoadBalancerClient(value = "coupon-template", configuration = CanaryRuleConfiguration.class)
public class CouponCustomerClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerClientApplication.class, args);
    }

}
