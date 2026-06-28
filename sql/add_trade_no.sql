-- 添加 trade_no 字段
ALTER TABLE payments ADD COLUMN trade_no VARCHAR(64) AFTER status;
