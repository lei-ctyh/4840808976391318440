package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SmsExamItem;
import com.ruoyi.system.service.ISmsExamItemService;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 考试项管理Controller
 * 
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/system/item")
public class SmsExamItemController extends BaseController
{
    @Autowired
    private ISmsExamItemService smsExamItemService;

    /**
     * 查询考试项管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:item:list')")
    @GetMapping("/list")
    public AjaxResult list(SmsExamItem smsExamItem)
    {
        List<SmsExamItem> list = smsExamItemService.selectSmsExamItemList(smsExamItem);
        return success(list);
    }

    /**
     * 导出考试项管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:item:export')")
    @Log(title = "考试项管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SmsExamItem smsExamItem)
    {
        List<SmsExamItem> list = smsExamItemService.selectSmsExamItemList(smsExamItem);
        ExcelUtil<SmsExamItem> util = new ExcelUtil<SmsExamItem>(SmsExamItem.class);
        util.exportExcel(response, list, "考试项管理数据");
    }

    /**
     * 获取考试项管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:item:query')")
    @GetMapping(value = "/{itemId}")
    public AjaxResult getInfo(@PathVariable("itemId") Long itemId)
    {
        return success(smsExamItemService.selectSmsExamItemByItemId(itemId));
    }

    /**
     * 新增考试项管理
     */
    @PreAuthorize("@ss.hasPermi('system:item:add')")
    @Log(title = "考试项管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SmsExamItem smsExamItem)
    {
        return toAjax(smsExamItemService.insertSmsExamItem(smsExamItem));
    }

    /**
     * 修改考试项管理
     */
    @PreAuthorize("@ss.hasPermi('system:item:edit')")
    @Log(title = "考试项管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SmsExamItem smsExamItem)
    {
        return toAjax(smsExamItemService.updateSmsExamItem(smsExamItem));
    }

    /**
     * 删除考试项管理
     */
    @PreAuthorize("@ss.hasPermi('system:item:remove')")
    @Log(title = "考试项管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return toAjax(smsExamItemService.deleteSmsExamItemByItemIds(itemIds));
    }
}
