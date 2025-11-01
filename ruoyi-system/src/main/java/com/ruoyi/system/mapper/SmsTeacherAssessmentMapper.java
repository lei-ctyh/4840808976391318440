package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SmsTeacherAssessment;

/**
 * 教师考核Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-10
 */
public interface SmsTeacherAssessmentMapper
{
    /**
     * 查询教师考核列表
     *
     * @param smsTeacherAssessment 教师考核
     * @return 教师考核集合
     */
    public List<SmsTeacherAssessment> selectSmsTeacherAssessmentList(SmsTeacherAssessment smsTeacherAssessment);

    /**
     * 批量插入教师考核数据
     *
     * @param assessmentList 教师考核数据列表
     * @return 结果
     */
    public int batchInsertSmsTeacherAssessment(List<SmsTeacherAssessment> assessmentList);

    /**
     * 根据人员ID和年度查询考核记录
     *
     * @param personId 人员ID
     * @param period 评定周期
     * @return 考核记录
     */
    public SmsTeacherAssessment selectByPersonIdAndPeriod(@Param("personId") String personId, @Param("period") String period);

    /**
     * 根据单位ID和年度查询考核记录列表
     *
     * @param unitId 单位ID
     * @param period 评定周期
     * @return 考核记录列表
     */
    public List<SmsTeacherAssessment> selectByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    /**
     * 新增教师考核
     *
     * @param smsTeacherAssessment 教师考核
     * @return 结果
     */
    public int insertSmsTeacherAssessment(SmsTeacherAssessment smsTeacherAssessment);

    /**
     * 修改教师考核
     *
     * @param smsTeacherAssessment 教师考核
     * @return 结果
     */
    public int updateSmsTeacherAssessment(SmsTeacherAssessment smsTeacherAssessment);

    /**
     * 统计指定单位和年份的评级分布
     *
     * @param unitId 单位ID
     * @param year 年份
     * @return 评级分布统计（rating, count）
     */
    public List<Map<String, Object>> selectRatingDistribution(@Param("unitId") String unitId, @Param("year") String year);
}