package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SmsStudentAssessment;
import com.ruoyi.system.service.ISmsStudentAssessmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/studentAssessment")
public class SmsStudentAssessmentController extends BaseController {

    @Resource
    private ISmsStudentAssessmentService studentService;

    /** 列表 */
    @GetMapping("/list")
    public TableDataInfo list(SmsStudentAssessment query) {
        startPage();
        List<SmsStudentAssessment> list = studentService.selectSmsStudentAssessmentList(query);
        return getDataTable(list);
    }

    /** 按人员与年度查询 */
    @GetMapping("/getByPersonIdAndPeriod")
    public AjaxResult getByPersonIdAndPeriod(@RequestParam String personId, @RequestParam String period) {
        SmsStudentAssessment data = studentService.selectByPersonIdAndPeriod(personId, period);
        return AjaxResult.success(data);
    }

    /** 按单位与年度查询 */
    @GetMapping("/getByUnitIdAndPeriod")
    public AjaxResult getByUnitIdAndPeriod(@RequestParam String unitId, @RequestParam String period) {
        List<SmsStudentAssessment> list = studentService.selectByUnitIdAndPeriod(unitId, period);
        return AjaxResult.success(list);
    }

    /** 导出 */
    @Log(title = "学生考核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public AjaxResult export(@RequestBody SmsStudentAssessment query) {
        List<SmsStudentAssessment> list = studentService.selectSmsStudentAssessmentList(query);
        ExcelUtil<SmsStudentAssessment> util = new ExcelUtil<>(SmsStudentAssessment.class);
        return util.exportExcel(list, "学生考核数据");
    }

    /** 下载导入模板 */
    @PostMapping("/importTemplate")
    public AjaxResult importTemplate() {
        ExcelUtil<SmsStudentAssessment> util = new ExcelUtil<>(SmsStudentAssessment.class);
        return util.importTemplateExcel("学生考核导入模板");
    }

    /** 导入数据 */
    @Log(title = "学生考核", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, @RequestParam(defaultValue = "false") boolean updateSupport) {
        String message = studentService.importStudentAssessment(file, updateSupport);
        return AjaxResult.success(message);
    }

    /** dashboard 数据：按年度与单位 */
    @GetMapping("/dashboard")
    public AjaxResult dashboard(@RequestParam("year") String period, @RequestParam("unitId") String unitId) {
        List<SmsStudentAssessment> list = studentService.selectByUnitIdAndPeriod(unitId, period);
        return AjaxResult.success(list);
    }
}