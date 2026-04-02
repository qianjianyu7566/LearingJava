package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    /**
     * API 网关启动入口。
     * <p>
     * 在这个骨架里：
     * - 根据路由规则把 /api/users/** 转发到 user-service（使用服务发现 lb://）
     * - Gateway 同时作为 Eureka 客户端，用于从注册中心获取服务地址
     * </p>
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(GatewayApplication.class, args);
    }
}

