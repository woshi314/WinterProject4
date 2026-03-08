use memo;

DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE tb_user (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号码',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nick_name` VARCHAR(32) DEFAULT NULL COMMENT '昵称,默认为用户id',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uniq_phone`(`phone`)
)ENGINE =InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `tb_memo`;
CREATE TABLE `tb_memo` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否完成',
  `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_memo_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci
ROW_FORMAT = Compact;

drop table tb_user;
drop table tb_memo;

delete from tb_user where id=1;