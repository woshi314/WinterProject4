-- 检查tb_memo表的update_time字段
SELECT id, title, completed, create_time, update_time FROM tb_memo WHERE user_id = 1 AND completed = 1 LIMIT 5;

-- 检查表结构
DESCRIBE tb_memo;
