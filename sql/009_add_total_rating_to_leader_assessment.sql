-- 为sms_leader_assessment表添加总评定字段
-- 执行日期：2025年

-- 添加总评定字段
ALTER TABLE `sms_leader_assessment` 
ADD COLUMN `total_rating` VARCHAR(20) DEFAULT NULL COMMENT '总评定（不合格、合格、良好、优秀）' 
AFTER `total_score`;

-- 更新表注释
ALTER TABLE `sms_leader_assessment` COMMENT = '领导成绩考核表';

-- 示例：根据总成绩更新总评定（可选）
-- UPDATE `sms_leader_assessment` 
-- SET `total_rating` = CASE 
--     WHEN `total_score` >= 90 THEN '优秀'
--     WHEN `total_score` >= 80 THEN '良好'
--     WHEN `total_score` >= 60 THEN '合格'
--     ELSE '不合格'
-- END
-- WHERE `total_score` IS NOT NULL;