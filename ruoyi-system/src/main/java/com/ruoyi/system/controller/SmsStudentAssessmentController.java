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
import com.ruoyi.system.domain.SmsStudentAssessment;
import com.ruoyi.system.service.ISmsStudentAssessmentService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 战士成绩考核Controller
 *
 * @author ruoyi
 * @date 2025-01-10
 */
@RestController
@RequestMapping("/system/studentAssessment")
public class SmsStudentAssessmentController extends BaseController
{
    @Autowired
    private ISmsStudentAssessmentService smsStudentAssessmentService;

    /**
     * 查询战士成绩考核列表
     */
    @PreAuthorize("@ss.hasPermi('system:studentAssessment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SmsStudentAssessment smsStudentAssessment)
    {
        startPage();
        List<SmsStudentAssessment> list = smsStudentAssessmentService.selectSmsStudentAssessmentList(smsStudentAssessment);
        return getDataTable(list);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     */
    @PreAuthorize("@ss.hasPermi('system:studentAssessment:query')")
    @GetMapping("/person/{personId}/period/{period}")
    public AjaxResult getByPersonIdAndPeriod(@PathVariable("personId") String personId, @PathVariable("period") String period)
    {
        SmsStudentAssessment assessment = smsStudentAssessmentService.selectByPersonIdAndPeriod(personId, period);
        return success(assessment);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:studentAssessment:query')")
    @GetMapping("/unit/{unitId}/period/{period}")
    public TableDataInfo getByUnitIdAndPeriod(@PathVariable("unitId") String unitId, @PathVariable("period") String period)
    {
        startPage();
        List<SmsStudentAssessment> list = smsStudentAssessmentService.selectByUnitIdAndPeriod(unitId, period);
        return getDataTable(list);
    }

    /**
     * 导入战士成绩考核数据
     */
    @PreAuthorize("@ss.hasPermi('system:studentAssessment:import')")
    @Log(title = "战士成绩考核", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, @RequestParam("unitId") String unitId) throws Exception
    {
        return smsStudentAssessmentService.importStudentAssessment(file, updateSupport, unitId);
    }


}