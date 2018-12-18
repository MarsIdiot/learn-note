

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_opt_log
-- ----------------------------
DROP TABLE IF EXISTS `t_opt_log`;
CREATE TABLE `t_opt_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(20) NOT NULL DEFAULT '' COMMENT '用户 ID',
  `ip` varchar(40) NOT NULL DEFAULT '' COMMENT 'IP 地址',
  `type` tinyint(2) unsigned NOT NULL COMMENT '调用类型（1：对内调用，2：对外调用）',
  `app_id` varchar(100) NOT NULL DEFAULT '' COMMENT '项目 ID',
  `app_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `interface_name` varchar(100) NOT NULL DEFAULT '' COMMENT '调用接口名',
  `method_name` varchar(100) NOT NULL DEFAULT '' COMMENT '调用方法名',
  `request_params` varchar(5000) NOT NULL DEFAULT '' COMMENT '传入参数',
  `response_params` varchar(5000) NOT NULL DEFAULT '' COMMENT '传出参数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_interface` (`interface_name`) USING BTREE COMMENT '接口名索引',
  KEY `idx_method` (`method_name`) USING BTREE COMMENT '方法名索引',
  KEY `idx_creatTime` (`create_time`) USING BTREE COMMENT '日志记录生成时间索引',
  KEY `idx_userId` (`user_id`) USING BTREE COMMENT '用户Id索引'
) ENGINE=InnoDB AUTO_INCREMENT=441 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志表';
