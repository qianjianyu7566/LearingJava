package com.example.springcloud;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user")
public class UserProperties {
    /**
     * 由配置项 `user.welcome-message` 映射而来。
     * <p>
     * 优先级（大致）：
     * - Config Server 返回的配置（application/profile 对应）
     * - 本地 application.yml 中的 user.welcome-message
     * </p>
     */
    private String welcomeMessage = "Hello from user-service (local).";

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}

