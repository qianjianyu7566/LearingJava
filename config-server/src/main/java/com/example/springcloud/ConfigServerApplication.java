package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    /**
     * Spring Cloud Config 配置中心的启动入口。
     * <p>
     * 它会把本地（native）配置仓库中的文件，按
     * - {application}-{profile}.yml（例如：user-service-dev.yml）
     * - 或 {application}.yml / {application}-{profile}.properties
     * 的规则提供给各微服务读取。
     * </p>
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

