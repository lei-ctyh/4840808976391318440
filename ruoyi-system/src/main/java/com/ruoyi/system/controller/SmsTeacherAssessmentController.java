package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SmsTeacherAssessment;
import com.ruoyi.system.service.ISmsTeacherAssessmentService;

/**
 * 教师考核Controller
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
@RestController
@RequestMapping("/system/teacher-assessment")
public class SmsTeacherAssessmentController extends BaseController
{
    @Autowired
    private ISmsTeacherAssessmentService smsTeacherAssessmentService;

    /**
     * 查询教师考核列表
     */
    @PreAuthorize("@ss.hasPermi('system:teacher-assessment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SmsTeacherAssessment smsTeacherAssessment)
    {
        startPage();
        List<SmsTeacherAssessment> list = smsTeacherAssessmentService.selectSmsTeacherAssessmentList(smsTeacherAssessment);
        return getDataTable(list);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     */
    @PreAuthorize("@ss.hasPermi('system:teacher-assessment:query')")
    @GetMapping("/person/{personId}/{period}")
    public AjaxResult getByPersonIdAndPeriod(@PathVariable("personId") String personId, 
                                           @PathVariable("period") String period)
    {
        SmsTeacherAssessment assessment = smsTeacherAssessmentService.selectByPersonIdAndPeriod(personId, period);
        return success(assessment);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:teacher-assessment:query')")
    @GetMapping("/unit/{unitId}/{period}")
    public AjaxResult getByUnitIdAndPeriod(@PathVariable("unitId") String unitId, 
                                         @PathVariable("period") String period)
    {
        List<SmsTeacherAssessment> list = smsTeacherAssessmentService.selectByUnitIdAndPeriod(unitId, period);
        return success(list);
    }

    /**
     * 导出教师考核列表
     */
    @PreAuthorize("@ss.hasPermi('system:teacher-assessment:export')")
    @Log(title = "教师考核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SmsTeacherAssessment smsTeacherAssessment)
    {
        List<SmsTeacherAssessment> list = smsTeacherAssessmentService.selectSmsTeacherAssessmentList(smsTeacherAssessment);
        ExcelUtil<SmsTeacherAssessment> util = new ExcelUtil<SmsTeacherAssessment>(SmsTeacherAssessment.class);
        util.exportExcel(response, list, "教师考核数据");
    }

    /**
     * 获取教师考核导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SmsTeacherAssessment> util = new ExcelUtil<SmsTeacherAssessment>(SmsTeacherAssessment.class);
        util.importTemplateExcel(response, "教师考核数据");
    }

    /**
     * 导入教师考核数据
     */
    @Log(title = "教师考核", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:teacher-assessment:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
                                 @RequestParam("unitId") String unitId,
                                 @RequestParam("year") String year) throws Exception
    {
        return smsTeacherAssessmentService.importTeacherAssessment(file, updateSupport, unitId, year);
    }
}