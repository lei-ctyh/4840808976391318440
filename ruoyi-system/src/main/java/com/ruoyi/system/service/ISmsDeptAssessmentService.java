package com.ruoyi.system.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SmsLeaderAssessment;

/**
 * 领导班子考核Service接口
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
public interface ISmsLeaderAssessmentService 
{
    /**
     * 查询领导班子考核列表
     * 
     * @param smsLeaderAssessment 领导班子考核
     * @return 领导班子考核集合
     */
    public List<SmsLeaderAssessment> selectSmsLeaderAssessmentList(SmsLeaderAssessment smsLeaderAssessment);

    /**
     * 根据人员ID和考核周期查询考核记录
     * 
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    public SmsLeaderAssessment selectByPersonIdAndPeriod(String personId, String period);

    /**
     * 根据单位ID和考核周期查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    public List<SmsLeaderAssessment> selectByUnitIdAndPeriod(String unitId, String period);

    /**
     * 导入领导班子考核数据
     * 
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    public AjaxResult importLeaderAssessment(MultipartFile file, boolean updateSupport) throws Exception;

}