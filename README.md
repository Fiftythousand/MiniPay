# MiniPay 支付平台

MiniPay 是一个模拟支付平台的最小可演示系统，覆盖"创建订单 → 发起支付 → 支付状态更新 → 结果查询"完整主链路。

## 技术栈

| 组件 | 技术 |
|------|------|
| 网关 | Spring Cloud Gateway (端口 8080) |
| 订单服务 | Spring Boot 3 + MyBatis-Plus + MySQL (端口 8081) |
| 支付服务 | Spring Boot 3 + MyBatis-Plus + MySQL (端口 8082) |
| 前端 | Vue 3 + Vite + Axios (端口 3000) |
| 数据库 | MySQL 8.0 |
| 容器化 | Docker + Docker Compose |

## 目录结构

```
MiniPay/
├── gateway/              # API 网关
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── order-service/        # 订单服务
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── payment-service/      # 支付服务
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/             # 前端页面
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml    # 一键启动
├── init-db.sql           # 数据库初始化
└── README.md
```

## 快速启动

### 方式一：Docker Compose 一键启动（推荐）

前置条件：已安装 Docker 和 Docker Compose。

```bash
docker-compose up --build
```

启动后访问：
- 前端页面：http://localhost:3000
- 网关：http://localhost:8080
- 订单服务：http://localhost:8081/api/orders/health
- 支付服务：http://localhost:8082/api/payments/health

### 方式二：本地开发启动

前置条件：JDK 17、Maven、Node.js 20+、MySQL 8.0。

1. 初始化数据库：
```sql
CREATE DATABASE IF NOT EXISTS minipay_order;
CREATE DATABASE IF NOT EXISTS minipay_payment;
```

2. 配置数据库密码，在启动服务前设置环境变量：
```bash
# Windows PowerShell
$env:MYSQL_PASSWORD = "你的MySQL密码"

# Linux/Mac
export MYSQL_PASSWORD="你的MySQL密码"
```
或直接修改 `order-service/src/main/resources/application.yml` 和 `payment-service/src/main/resources/application.yml` 中的 `password` 字段。

2. 启动订单服务：
```bash
cd order-service
mvn spring-boot:run
```

3. 启动支付服务：
```bash
cd payment-service
mvn spring-boot:run
```

4. 启动网关：
```bash
cd gateway
mvn spring-boot:run
```

5. 启动前端：
```bash
cd frontend
npm install
npm run dev
```

## API 接口

### 订单服务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders | 创建订单 |
| GET | /api/orders/{orderId} | 查询订单 |
| GET | /api/orders/health | 健康检查 |

### 支付服务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/payments | 发起支付 |
| GET | /api/payments/{orderId} | 查询支付状态 |
| GET | /api/payments/health | 健康检查 |

## 主链路流程

1. 用户在前端输入金额和描述，创建订单
2. 输入订单号和金额，发起模拟支付（80%概率成功）
3. 在结果查询页输入订单号，查看订单和支付状态
