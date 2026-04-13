package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UserProperties.class)
public class UserServiceApplication {
    /**
     * user-service 服务启动入口。
     * <p>
     * 它做两件事：
     * - 通过 Eureka 注册自己（便于网关/其他服务通过服务名访问）
     * - 通过 Config Server 读取配置（例如 user.welcome-message）
     * </p>
     */
    public static void main(String[] args) {
        // 启动 Spring Boot，并开启 @ConfigurationProperties 映射到 UserProperties
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

