package com.ruoyi.system.service;

import com.ruoyi.system.domain.SmsStudentAssessment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 学生成绩考核 Service 接口
 */
public interface ISmsStudentAssessmentService {

    /** 查询学生考核列表 */
    List<SmsStudentAssessment> selectSmsStudentAssessmentList(SmsStudentAssessment query);

    /** 按人员与年度查询 */
    SmsStudentAssessment selectByPersonIdAndPeriod(String personId, String period);

    /** 按单位与年度查询 */
    List<SmsStudentAssessment> selectByUnitIdAndPeriod(String unitId, String period);

    /** 导入学生考核数据（Excel），支持更新 */
    String importStudentAssessment(MultipartFile file, boolean updateSupport);
}