package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SmsDeptAssessment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单位成绩考核Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-27
 */
public interface SmsDeptAssessmentMapper 
{
    /**
     * 查询单位成绩考核列表
     * 
     * @param smsDeptAssessment 单位成绩考核
     * @return 单位成绩考核集合
     */
    public List<SmsDeptAssessment> selectSmsDeptAssessmentList(SmsDeptAssessment smsDeptAssessment);

    /**
     * 批量插入单位成绩考核数据
     * 
     * @param assessmentList 单位成绩考核数据列表
     * @return 结果
     */
    public int batchInsertSmsDeptAssessment(List<SmsDeptAssessment> assessmentList);

    /**
     * 根据人员ID和年度查询考核记录
     * 
     * @param personId 人员ID
     * @param period 评定周期
     * @return 考核记录
     */
    public SmsDeptAssessment selectByPersonIdAndPeriod(@Param("personId") String personId, @Param("period") String period);

    /**
     * 根据单位ID和年度查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 评定周期
     * @return 考核记录列表
     */
    public List<SmsDeptAssessment> selectByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    /**
     * 新增单位成绩考核
     * 
     * @param smsDeptAssessment 单位成绩考核
     * @return 结果
     */
    public int insertSmsDeptAssessment(SmsDeptAssessment smsDeptAssessment);

    /**
     * 修改单位成绩考核
     * 
     * @param smsDeptAssessment 单位成绩考核
     * @return 结果
     */
    public int updateSmsDeptAssessment(SmsDeptAssessment smsDeptAssessment);
}