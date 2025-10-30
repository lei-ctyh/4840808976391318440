package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SmsStudentAssessment;

/**
 * 学生考核Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-10
 */
public interface SmsStudentAssessmentMapper {
    /**
     * 查询学生考核列表
     *
     * @param smsStudentAssessment 学生考核
     * @return 学生考核集合
     */
    List<SmsStudentAssessment> selectSmsStudentAssessmentList(SmsStudentAssessment smsStudentAssessment);

    /**
     * 批量插入学生考核数据
     *
     * @param assessmentList 学生考核数据列表
     * @return 结果
     */
    int batchInsertSmsStudentAssessment(List<SmsStudentAssessment> assessmentList);

    /**
     * 根据人员ID和评定周期查询考核记录
     */
    SmsStudentAssessment selectByPersonIdAndPeriod(@Param("personId") String personId, @Param("period") String period);

    /**
     * 根据单位ID和评定周期查询考核记录列表
     */
    List<SmsStudentAssessment> selectByUnitIdAndPeriod(@Param("unitId") String unitId, @Param("period") String period);

    /** 新增学生考核 */
    int insertSmsStudentAssessment(SmsStudentAssessment smsStudentAssessment);

    /** 修改学生考核 */
    int updateSmsStudentAssessment(SmsStudentAssessment smsStudentAssessment);
}