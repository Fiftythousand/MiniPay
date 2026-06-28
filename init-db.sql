CREATE DATABASE IF NOT EXISTS minipay_order;
USE minipay_order;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME,
    updated_at DATETIME
);

CREATE DATABASE IF NOT EXISTS minipay_payment;
USE minipay_payment;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL,
    order_id VARCHAR(64) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    trade_no VARCHAR(64),
    paid_at DATETIME,
    created_at DATETIME
);

-- 创建 Nacos 数据库
CREATE DATABASE IF NOT EXISTS nacos_config;
