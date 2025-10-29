package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SmsTemplate;

/**
 * 模板绑定Mapper接口
 */
public interface SmsTemplateMapper {

    public SmsTemplate selectSmsTemplateById(Long templateId);

    public List<SmsTemplate> selectSmsTemplateList(SmsTemplate smsTemplate);

    public int insertSmsTemplate(SmsTemplate smsTemplate);

    public int updateSmsTemplate(SmsTemplate smsTemplate);

    public int deleteSmsTemplateById(Long templateId);

    public int deleteSmsTemplateByIds(Long[] templateIds);

    /**
     * 根据唯一键查询模板
     * @param orgCode 机构编码
     * @param boardType 看板类型
     * @param year 年度
     * @return 模板信息
     */
    public SmsTemplate selectByUnique(@Param("orgCode") String orgCode,
                                      @Param("boardType") String boardType,
                                      @Param("year") Integer year);
}