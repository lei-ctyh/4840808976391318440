package com.ruoyi.system.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SmsTeacherAssessment;

/**
 * 教师考核Service接口
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
public interface ISmsTeacherAssessmentService 
{
    /**
     * 查询教师考核列表
     * 
     * @param smsTeacherAssessment 教师考核
     * @return 教师考核集合
     */
    public List<SmsTeacherAssessment> selectSmsTeacherAssessmentList(SmsTeacherAssessment smsTeacherAssessment);

    /**
     * 根据人员ID和考核周期查询考核记录
     * 
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    public SmsTeacherAssessment selectByPersonIdAndPeriod(String personId, String period);

    /**
     * 根据单位ID和考核周期查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    public List<SmsTeacherAssessment> selectByUnitIdAndPeriod(String unitId, String period);

    /**
     * 导入教师考核数据
     * 
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    public AjaxResult importTeacherAssessment(MultipartFile file, boolean updateSupport, String unitId) throws Exception;
}