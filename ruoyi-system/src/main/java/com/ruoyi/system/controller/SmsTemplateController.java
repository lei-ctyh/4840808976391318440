package com.ruoyi.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SmsTemplate;
import com.ruoyi.system.service.ISmsTemplateService;

/**
 * 模板管理接口
 * 路由：/sms/template
 */
@RestController
@RequestMapping("/sms/template")
public class SmsTemplateController extends BaseController {

    @Autowired
    private ISmsTemplateService smsTemplateService;

    /**
     * 绑定/更新模板（前端需先走 /common/upload 获取文件信息）
     */
    @Log(title = "模板绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult bind(@RequestBody SmsTemplate template) {
        if (template.getOrgCode() == null || template.getYear() == null || template.getBoardType() == null) {
            return AjaxResult.error("orgCode/year/boardType 不能为空");
        }
        if (template.getFilePath() == null || template.getFileName() == null) {
            return AjaxResult.error("请先上传文件后再绑定（缺少 filePath/fileName）");
        }
        int rows = smsTemplateService.bindTemplate(template);
        return toAjax(rows);
    }

    /**
     * 解析模板（从当前机构向上回溯查找）
     */
    @GetMapping("/resolve")
    public AjaxResult resolve(@RequestParam("orgCode") String orgCode,
                              @RequestParam("boardType") String boardType,
                              @RequestParam("year") Integer year) {
        SmsTemplate hit = smsTemplateService.resolveTemplate(orgCode, boardType, year);
        if (hit == null) {
            return AjaxResult.success("未在机构链找到可用模板");
        }
        return AjaxResult.success(hit);
    }

    /**
     * 是否存在（严格匹配当前机构）
     */
    @GetMapping("/exists")
    public AjaxResult exists(@RequestParam("orgCode") String orgCode,
                             @RequestParam("boardType") String boardType,
                             @RequestParam("year") Integer year) {
        boolean ok = smsTemplateService.exists(orgCode, boardType, year);
        return AjaxResult.success(ok);
    }

    /**
     * 查询模板信息（严格匹配当前机构）
     * 用于前端模板上传组件回显已有模板信息
     */
    @GetMapping("/get")
    public AjaxResult getTemplate(@RequestParam("orgCode") String orgCode,
                                  @RequestParam("boardType") String boardType,
                                  @RequestParam("year") Integer year) {
        if (orgCode == null || boardType == null || year == null) {
            return AjaxResult.error("orgCode、boardType、year 参数不能为空");
        }
        
        SmsTemplate template = smsTemplateService.getTemplate(orgCode, boardType, year);
        if (template == null) {
            return AjaxResult.success("未找到对应的模板", null);
        }
        
        return AjaxResult.success("查询成功", template);
    }
}