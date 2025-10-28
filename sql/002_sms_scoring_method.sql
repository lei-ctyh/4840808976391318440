-- -------------------------------------------------------------
-- 核定方式字典：用于考试项管理的评分方式选择
-- 包含：1 百分制、2 合格制、3 取均值
-- -------------------------------------------------------------

-- 清理已存在的相同字典，防止重复执行导致重复数据
delete from sys_dict_data where dict_type = 'sms_scoring_method';
delete from sys_dict_type where dict_type = 'sms_scoring_method';

-- 字典类型
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
values ('核定方式', 'sms_scoring_method', '0', 'admin', sysdate(), '', null, '考试项核定方式字典');

-- 字典数据
insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
values 
  (1, '百分制', '1', 'sms_scoring_method', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '按百分制计分'),
  (2, '合格制', '2', 'sms_scoring_method', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '判定合格/不合格'),
  (3, '取均值', '3', 'sms_scoring_method', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '对子项成绩求均值');