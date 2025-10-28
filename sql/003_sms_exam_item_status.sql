-- -------------------------------------------------------------
-- 考核（考试）项状态字典
-- 用于 sms_exam_item.status 的字典映射：0 正常，1 停用
-- -------------------------------------------------------------

-- 清理已存在的相同字典，防止重复执行导致重复数据
delete from sys_dict_data where dict_type = 'sms_exam_item_status';
delete from sys_dict_type where dict_type = 'sms_exam_item_status';

-- 字典类型
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
values ('考试项状态', 'sms_exam_item_status', '0', 'admin', sysdate(), '', null, '考试项状态：0正常 1停用');

-- 字典数据
insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
values 
  (1, '正常', '0', 'sms_exam_item_status', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态'),
  (2, '停用', '1', 'sms_exam_item_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');