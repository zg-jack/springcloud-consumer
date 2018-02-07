package com.zhuguang.jack.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.zhuguang.jack"})
//注册到eureka
@EnableEurekaClient
//开启断路器功能
@EnableCircuitBreaker
//@SpringCloudApplication
public class SpringBootSampleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringBootSampleApplication.class, args);
    }
    
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
