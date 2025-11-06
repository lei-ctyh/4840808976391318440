package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SmsLeaderAssessment;

/**
 * 领导班子考核Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-27
 */
public interface SmsLeaderAssessmentMapper 
{
    /**
     * 查询领导班子考核列表
     * 
     * @param smsLeaderAssessment 领导班子考核
     * @return 领导班子考核集合
     */
    public List<SmsLeaderAssessment> selectSmsLeaderAssessmentList(SmsLeaderAssessment smsLeaderAssessment);

    /**
     * 批量插入领导班子考核数据
     * 
     * @param assessmentList 领导班子考核数据列表
     * @return 结果
     */
    public int batchInsertSmsLeaderAssessment(List<SmsLeaderAssessment> assessmentList);

    /**
     * 根据人员ID和年度查询考核记录
     * 
     * @param personId 人员ID
     * @param period 评定周期
     * @return 考核记录
     */
    public SmsLeaderAssessment selectByPersonIdAndPeriod(@Param("personId") String personId, @Param("period") String period);

    /**
     * 根据单位ID和年度查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 评定周期
     * @return 考核记录列表
     */
    public List<SmsLeaderAssessment> selectByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    public int deleteByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    /**
     * 新增领导班子考核
     * 
     * @param smsLeaderAssessment 领导班子考核
     * @return 结果
     */
    public int insertSmsLeaderAssessment(SmsLeaderAssessment smsLeaderAssessment);

    /**
     * 修改领导班子考核
     * 
     * @param smsLeaderAssessment 领导班子考核
     * @return 结果
     */
    public int updateSmsLeaderAssessment(SmsLeaderAssessment smsLeaderAssessment);
}