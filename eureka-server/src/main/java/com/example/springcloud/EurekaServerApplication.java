package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    /**
     * Eureka 服务注册中心的启动入口。
     * <p>
     * - 作为注册中心：接收各业务服务（如 user-service）的注册/心跳
     * - 对外暴露 Web 控制台（默认端口 8761）
     * </p>
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

