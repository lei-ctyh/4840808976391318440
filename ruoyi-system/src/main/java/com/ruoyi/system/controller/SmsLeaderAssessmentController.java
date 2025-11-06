package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SmsLeaderAssessment;
import com.ruoyi.system.service.ISmsLeaderAssessmentService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 领导班子考核Controller
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
@RestController
@RequestMapping("/system/leaderAssessment")
public class SmsLeaderAssessmentController extends BaseController
{
    @Autowired
    private ISmsLeaderAssessmentService smsLeaderAssessmentService;

    /**
     * 查询领导班子考核列表
     */
    @PreAuthorize("@ss.hasPermi('system:leaderAssessment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SmsLeaderAssessment smsLeaderAssessment)
    {
        startPage();
        List<SmsLeaderAssessment> list = smsLeaderAssessmentService.selectSmsLeaderAssessmentList(smsLeaderAssessment);
        return getDataTable(list);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     */
    @PreAuthorize("@ss.hasPermi('system:leaderAssessment:query')")
    @GetMapping("/person/{personId}/period/{period}")
    public AjaxResult getByPersonIdAndPeriod(@PathVariable("personId") String personId, @PathVariable("period") String period)
    {
        SmsLeaderAssessment assessment = smsLeaderAssessmentService.selectByPersonIdAndPeriod(personId, period);
        return success(assessment);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:leaderAssessment:query')")
    @GetMapping("/unit/{unitId}/period/{period}")
    public TableDataInfo getByUnitIdAndPeriod(@PathVariable("unitId") String unitId, @PathVariable("period") String period)
    {
        startPage();
        List<SmsLeaderAssessment> list = smsLeaderAssessmentService.selectByUnitIdAndPeriod(unitId, period);
        return getDataTable(list);
    }

    /**
     * 导入领导班子考核数据
     */
    @PreAuthorize("@ss.hasPermi('system:leaderAssessment:import')")
    @Log(title = "领导班子考核", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, @RequestParam("unitId") String unitId) throws Exception
    {
        return smsLeaderAssessmentService.importLeaderAssessment(file, updateSupport, unitId);
    }


}