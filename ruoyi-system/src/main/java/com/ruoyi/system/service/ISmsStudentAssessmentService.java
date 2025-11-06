package com.ruoyi.system.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SmsStudentAssessment;

/**
 * 战士成绩考核Service接口
 *
 * @author ruoyi
 * @date 2025-01-10
 */
public interface ISmsStudentAssessmentService
{
    /**
     * 查询战士成绩考核列表
     *
     * @param smsStudentAssessment 战士成绩考核
     * @return 战士成绩考核集合
     */
    public List<SmsStudentAssessment> selectSmsStudentAssessmentList(SmsStudentAssessment smsStudentAssessment);

    /**
     * 根据人员ID和考核周期查询考核记录
     *
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    public SmsStudentAssessment selectByPersonIdAndPeriod(String personId, String period);

    /**
     * 根据单位ID和考核周期查询考核记录列表
     *
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    public List<SmsStudentAssessment> selectByUnitIdAndPeriod(String unitId, String period);

    /**
     * 导入战士成绩考核数据
     *
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    public AjaxResult importStudentAssessment(MultipartFile file, boolean updateSupport, String unitId) throws Exception;

}