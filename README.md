# LearingJava（Spring Cloud 基础骨架）

这是一个 Spring Cloud 微服务学习用的基础工程骨架（Maven 多模块），包含：

- `eureka-server`：服务注册中心（Eureka）
- `config-server`：配置中心（Spring Cloud Config，native 模式读取本地 `config-repo`）
- `gateway`：API 网关（Spring Cloud Gateway，基于 Eureka 的 `lb://` 转发）
- `user-service`：示例业务服务（对外提供 `/api/users/hello`，并演示从 Config Server 读取配置）

## 项目结构

```text
LearingJava/
  pom.xml                       # 父工程（聚合模块）
  eureka-server/
  config-server/
  gateway/
  user-service/
  config-repo/                  # config-server 的配置仓库（native 模式读取）
```

## 配置中心如何工作

`config-server` 使用 native 模式读取 `config-repo` 目录。

`user-service` 配置：

- 服务名：`spring.application.name = user-service`
- profile：`spring.profiles.active = dev`

因此 Config Server 会优先查找并返回：

- `config-repo/user-service-dev.yml`

## 启动顺序（建议）

1. 启动 `eureka-server`（默认端口 `8761`）
2. 启动 `config-server`（默认端口 `8888`）
3. 启动 `user-service`
4. 启动 `gateway`（默认端口 `8080`）

> 其中 `user-service` 的 `server.port` 配置为 `0`，表示随机端口启动；Eureka 会负责将服务地址注册给网关使用。

## 调用示例

当网关启动后，请访问：

- `GET http://localhost:8080/api/users/hello`

返回内容示例：

```json
{ "message": "Hello from Spring Cloud Config Server (dev)." }
```
