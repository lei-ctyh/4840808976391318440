package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SmsDeptAssessment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 单位成绩考核Service接口
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
public interface ISmsDeptAssessmentService 
{
    /**
     * 查询单位成绩考核列表
     * 
     * @param smsDeptAssessment 单位成绩考核
     * @return 单位成绩考核集合
     */
    public List<SmsDeptAssessment> selectSmsDeptAssessmentList(SmsDeptAssessment smsDeptAssessment);

    /**
     * 根据人员ID和考核周期查询考核记录
     * 
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    public SmsDeptAssessment selectByPersonIdAndPeriod(String personId, String period);

    /**
     * 根据单位ID和考核周期查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    public List<SmsDeptAssessment> selectByUnitIdAndPeriod(String unitId, String period);

    /**
     * 导入单位成绩考核数据
     * 
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    public AjaxResult importDeptAssessment(MultipartFile file, boolean updateSupport, String unitId, String year) throws Exception;

}