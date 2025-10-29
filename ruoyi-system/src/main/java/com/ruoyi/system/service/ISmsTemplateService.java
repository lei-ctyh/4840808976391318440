package com.ruoyi.system.service;

import com.ruoyi.system.domain.SmsTemplate;

/**
 * 模板绑定Service接口
 */
public interface ISmsTemplateService {

    /**
     * 绑定/更新模板（同 orgId+boardType+year 唯一）
     */
    int bindTemplate(SmsTemplate template);

    /**
     * 解析模板（向上查找父机构）
     * @param orgCode 机构编码
     * @param boardType 看板类型
     * @param year 年度
     * @return 模板信息
     */
    SmsTemplate resolveTemplate(String orgCode, String boardType, Integer year);

    /**
     * 检查模板是否存在
     * @param orgCode 机构编码
     * @param boardType 看板类型
     * @param year 年度
     * @return 是否存在
     */
    boolean exists(String orgCode, String boardType, Integer year);
}