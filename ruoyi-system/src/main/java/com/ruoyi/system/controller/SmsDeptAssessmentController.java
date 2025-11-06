package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SmsDeptAssessment;
import com.ruoyi.system.service.ISmsDeptAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 单位成绩考核Controller
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
@RestController
@RequestMapping("/system/deptAssessment")
public class SmsDeptAssessmentController extends BaseController
{
    @Autowired
    private ISmsDeptAssessmentService smsDeptAssessmentService;

    /**
     * 查询单位成绩考核列表
     */
    @PreAuthorize("@ss.hasPermi('system:deptAssessment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SmsDeptAssessment smsDeptAssessment)
    {
        startPage();
        List<SmsDeptAssessment> list = smsDeptAssessmentService.selectSmsDeptAssessmentList(smsDeptAssessment);
        return getDataTable(list);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     */
    @PreAuthorize("@ss.hasPermi('system:deptAssessment:query')")
    @GetMapping("/person/{personId}/period/{period}")
    public AjaxResult getByPersonIdAndPeriod(@PathVariable("personId") String personId, @PathVariable("period") String period)
    {
        SmsDeptAssessment assessment = smsDeptAssessmentService.selectByPersonIdAndPeriod(personId, period);
        return success(assessment);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:deptAssessment:query')")
    @GetMapping("/unit/{unitId}/period/{period}")
    public TableDataInfo getByUnitIdAndPeriod(@PathVariable("unitId") String unitId, @PathVariable("period") String period)
    {
        startPage();
        List<SmsDeptAssessment> list = smsDeptAssessmentService.selectByUnitIdAndPeriod(unitId, period);
        return getDataTable(list);
    }

    /**
     * 导入单位成绩考核数据
     */
    @PreAuthorize("@ss.hasPermi('system:deptAssessment:import')")
    @Log(title = "单位成绩考核", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, @RequestParam("unitId") String unitId) throws Exception
    {
        return smsDeptAssessmentService.importDeptAssessment(file, updateSupport, unitId);
    }


}