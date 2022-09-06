package com.bruce.coupon.customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bruce"})
@EnableDiscoveryClient
public class CouponCustomerClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerClientApplication.class, args);
    }

}
