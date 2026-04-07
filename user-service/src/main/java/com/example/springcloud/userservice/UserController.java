package com.example.springcloud.userservice;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    /**
     * 通过构造器注入配置对象，避免在控制器内部直接读取 Environment。
     */
    private final UserProperties userProperties;

    public UserController(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    /**
     * 演示接口：
     * - 网关转发到 user-service 后可访问
     * - 返回的 message 来自 user-service 配置（优先来自 Config Server）
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        // 使用 Map 返回 JSON 格式：{"message":"..."}
        return Map.of("message", userProperties.getWelcomeMessage());
    }


}

