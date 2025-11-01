package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.DashboardStatisticsDTO;

/**
 * 考核统计服务接口
 *
 * @author ruoyi
 */
public interface IAssessmentStatisticsService
{
    /**
     * 获取看板统计数据
     *
     * @param orgCode 组织编码
     * @param year 年份
     * @return 看板统计数据
     */
    DashboardStatisticsDTO getDashboardStatistics(String orgCode, String year);
}
