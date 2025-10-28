-- 为部门表增加机构编码字段，必填且唯一
-- 说明：
-- 1) 先新增列为可空，填充现有数据（用 dept_id 作为默认编码），再改为非空并加唯一索引。
-- 2) 这是 MySQL 语法；如使用其它数据库，请按需调整。

-- 1. 新增列（先允许为空，便于迁移现有数据）
ALTER TABLE sys_dept
  ADD COLUMN org_code VARCHAR(64) NULL COMMENT '机构编码';

-- 2. 为现有记录填充唯一值（使用部门ID作为机构编码）
UPDATE sys_dept
  SET org_code = 0
  WHERE dept_id = '100';

-- 3. 将列改为非空
ALTER TABLE sys_dept
  MODIFY COLUMN org_code VARCHAR(64) NOT NULL COMMENT '机构编码';

-- 4. 创建唯一索引，保证机构编码不重复
CREATE UNIQUE INDEX uk_sys_dept_org_code ON sys_dept(org_code);