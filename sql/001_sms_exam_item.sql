-- -------------------------------------------------------------
-- 考试项管理表结构
-- -------------------------------------------------------------

drop table if exists sms_exam_item;

create table sms_exam_item (
    item_id           bigint(20)      not null auto_increment    comment '考试项ID',
    parent_id         bigint(20)      default 0                  comment '父考试项ID',
    item_name         varchar(30)     not null                   comment '考试项名称',
    order_num         int(4)          default 0                  comment '显示顺序',
    status            char(1)         default '0'                comment '状态（0正常 1停用）',
    scoring_method    char(1)         default '1'                comment '核定方式（1百分制 2合格制 3取均值）',
    remark            varchar(500)    default null               comment '备注',
    primary key (item_id)
) engine=innodb auto_increment=100 comment = '考试项管理表';

