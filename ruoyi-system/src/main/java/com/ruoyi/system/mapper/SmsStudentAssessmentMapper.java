package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SmsStudentAssessment;

/**
 * 学生考核Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-27
 */
public interface SmsStudentAssessmentMapper
{
    /**
     * 查询学生考核列表
     *
     * @param smsStudentAssessment 学生考核
     * @return 学生考核集合
     */
    public List<SmsStudentAssessment> selectSmsStudentAssessmentList(SmsStudentAssessment smsStudentAssessment);

    /**
     * 批量插入学生考核数据
     *
     * @param assessmentList 学生考核数据列表
     * @return 结果
     */
    public int batchInsertSmsStudentAssessment(List<SmsStudentAssessment> assessmentList);

    /**
     * 根据人员ID和年度查询考核记录
     *
     * @param personId 人员ID
     * @param period 评定周期
     * @return 考核记录
     */
    public SmsStudentAssessment selectByPersonIdAndPeriod(@Param("personId") String personId, @Param("period") String period);

    /**
     * 根据单位ID和年度查询考核记录列表
     *
     * @param unitId 单位ID
     * @param period 评定周期
     * @return 考核记录列表
     */
    public List<SmsStudentAssessment> selectByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    /**
     * 新增学生考核
     *
     * @param smsStudentAssessment 学生考核
     * @return 结果
     */
    public int insertSmsStudentAssessment(SmsStudentAssessment smsStudentAssessment);

    /**
     * 修改学生考核
     *
     * @param smsStudentAssessment 学生考核
     * @return 结果
     */
    public int updateSmsStudentAssessment(SmsStudentAssessment smsStudentAssessment);

    /**
     * 统计指定单位和年份的评级分布
     *
     * @param unitId 单位ID
     * @param year 年份
     * @return 评级分布统计（rating, count）
     */
    public List<Map<String, Object>> selectRatingDistribution(@Param("unitId") String unitId, @Param("year") String year);
}