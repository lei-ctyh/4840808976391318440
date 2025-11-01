package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.DashboardStatisticsDTO;
import com.ruoyi.system.service.IAssessmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 考核统计Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/assessment")
public class AssessmentStatisticsController extends BaseController
{
    @Autowired
    private IAssessmentStatisticsService assessmentStatisticsService;

    /**
     * 获取看板统计数据
     *
     * @param orgCode 组织编码
     * @param year 年份
     * @return 看板统计数据
     */
    @PreAuthorize("@ss.hasPermi('system:assessment:query')")
    @GetMapping("/dashboard/statistics")
    public AjaxResult getDashboardStatistics(@RequestParam String orgCode, @RequestParam String year)
    {
        DashboardStatisticsDTO statistics = assessmentStatisticsService.getDashboardStatistics(orgCode, year);
        return success(statistics);
    }
}
