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

> 其中 `user-service` 默认监听固定端口（Docker/部署更方便），通过 Eureka 注册后，网关使用 `lb://user-service` 转发。

## 调用示例

当网关启动后，请访问：

- `GET http://localhost:8080/api/users/hello`

返回内容示例：

```json
{ "message": "Hello from Spring Cloud Config Server (dev)." }
```

## Docker 运行与高并发（参考 1 万 QPS）

### 1. 构建并启动（单实例起步）

在项目根目录执行：

```bash
docker compose up --build -d
```

调用示例：

- `GET http://localhost:8080/api/users/hello`

### 2. 横向扩容 user-service（关键）

本骨架下接口本身非常轻量（每次请求主要是读取已加载的配置并返回 JSON），所以主要瓶颈在吞吐与网络转发。

建议把 `user-service` 扩容到足够数量（例如从 5、10、20 逐步加）：

```bash
docker compose up -d --scale user-service=10
```

由于 `user-service` 在 `docker-compose.yml` 中不映射宿主机端口，扩容不会端口冲突；网关通过 Eureka 发现并轮询到各实例。

### 3. 如果仍达不到 1 万 QPS

通常需要继续做两类增强：

- 扩容网关：`gateway` 也需要多个实例（现实生产一般再加 Nginx/Ingress 做入口负载均衡）
- JVM/线程调优：继续增大 `user-service` 的 `server.tomcat.threads.max`、网关的连接池参数（可根据压测结果调整）

> 1 万 QPS 最终能否达到，强依赖你本机/容器的 CPU 核心数、网络环境以及 Docker 的资源分配。
