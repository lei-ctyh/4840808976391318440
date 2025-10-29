-- -------------------------------------------------------------
-- 模板绑定表：按年、组织、看板类型存储模板元数据
-- 约束：同一年 + 组织 + 看板类型仅允许一条生效记录
-- 看板类型取值：leader / student / teacher / org
-- 文件限制：xls/xlsx，最大 50MB（由上传接口/网关校验）
-- -------------------------------------------------------------

DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template` (
  `template_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_code`    VARCHAR(64) NOT NULL COMMENT '机构编码（sys_dept.org_code）',
  `board_type`  VARCHAR(16) NOT NULL COMMENT '看板类型（leader/student/teacher/org）',
  `year`        INT NOT NULL COMMENT '年度（如 2025）',
  `file_name`   VARCHAR(255) NOT NULL COMMENT '文件原始名',
  `file_path`   VARCHAR(512) NOT NULL COMMENT '文件服务器相对路径/URL',
  `file_ext`    VARCHAR(16)  DEFAULT NULL COMMENT '文件后缀（xls/xlsx）',
  `file_size`   BIGINT       DEFAULT NULL COMMENT '文件大小（字节）',
  `status`      CHAR(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_org_type_year` (`org_code`, `board_type`, `year`),
  KEY `idx_type_year` (`board_type`, `year`),
  KEY `idx_org_code` (`org_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板绑定表（按年/组织/看板类型）';