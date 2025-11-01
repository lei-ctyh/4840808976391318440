package com.ruoyi.system.domain.dto;

import java.util.List;

/**
 * 看板统计数据 DTO
 *
 * @author ruoyi
 */
public class DashboardStatisticsDTO
{
    /** 当前单位指标 */
    private CurrentMetricsDTO currentMetrics;

    /** 下级单位对比数据 */
    private List<ChildComparisonDTO> childrenComparisons;

    /** 年度趋势数据 */
    private YearlyTrendDTO yearlyTrend;

    public DashboardStatisticsDTO()
    {
    }

    public DashboardStatisticsDTO(CurrentMetricsDTO currentMetrics, List<ChildComparisonDTO> childrenComparisons,
                                  YearlyTrendDTO yearlyTrend)
    {
        this.currentMetrics = currentMetrics;
        this.childrenComparisons = childrenComparisons;
        this.yearlyTrend = yearlyTrend;
    }

    public CurrentMetricsDTO getCurrentMetrics()
    {
        return currentMetrics;
    }

    public void setCurrentMetrics(CurrentMetricsDTO currentMetrics)
    {
        this.currentMetrics = currentMetrics;
    }

    public List<ChildComparisonDTO> getChildrenComparisons()
    {
        return childrenComparisons;
    }

    public void setChildrenComparisons(List<ChildComparisonDTO> childrenComparisons)
    {
        this.childrenComparisons = childrenComparisons;
    }

    public YearlyTrendDTO getYearlyTrend()
    {
        return yearlyTrend;
    }

    public void setYearlyTrend(YearlyTrendDTO yearlyTrend)
    {
        this.yearlyTrend = yearlyTrend;
    }
}
