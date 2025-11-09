package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.SmsDeptAssessment;
import com.ruoyi.system.domain.dto.ChildComparisonDTO;
import com.ruoyi.system.domain.dto.CurrentMetricsDTO;
import com.ruoyi.system.domain.dto.DashboardStatisticsDTO;
import com.ruoyi.system.domain.dto.YearlyTrendDTO;
import com.ruoyi.system.mapper.SmsDeptAssessmentMapper;
import com.ruoyi.system.mapper.SmsStudentAssessmentMapper;
import com.ruoyi.system.mapper.SmsTeacherAssessmentMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.IAssessmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考核统计服务实现
 *
 * @author ruoyi
 */
@Service
public class AssessmentStatisticsServiceImpl implements IAssessmentStatisticsService
{
    @Autowired
    private SmsDeptAssessmentMapper deptAssessmentMapper;

    @Autowired
    private SmsTeacherAssessmentMapper teacherAssessmentMapper;

    @Autowired
    private SmsStudentAssessmentMapper studentAssessmentMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 获取看板统计数据
     *
     * @param orgCode 组织编码
     * @param year 年份
     * @return 看板统计数据
     */
    @Override
    public DashboardStatisticsDTO getDashboardStatistics(String orgCode, String year)
    {
        // 1. 获取当前单位信息
        SysDept currentDept = deptMapper.selectDeptByOrgCode(orgCode);
        if (currentDept == null)
        {
            return createEmptyStatistics();
        }

        // 2. 获取当前单位指标
        CurrentMetricsDTO currentMetrics = getCurrentMetrics(orgCode, currentDept.getDeptId(), year);

        // 3. 获取下级单位对比数据
        List<ChildComparisonDTO> childrenComparisons = getChildrenComparisons(currentDept.getDeptId(), year);

        // 4. 获取年度趋势数据
        YearlyTrendDTO yearlyTrend = getYearlyTrend(orgCode, year);

        // 5. 组装返回数据
        return new DashboardStatisticsDTO(currentMetrics, childrenComparisons, yearlyTrend);
    }

    /**
     * 获取当前单位指标
     */
    private CurrentMetricsDTO getCurrentMetrics(String orgCode, Long deptId, String year)
    {
        // 从部门考核表查询综合成绩和评定
        SmsDeptAssessment deptAssessment = deptAssessmentMapper.selectByPersonIdAndYear(orgCode, year);

        Double avgScore = 0.0;
        if (deptAssessment != null && deptAssessment.getTotalScore() != null)
        {
            try
            {
                avgScore = Double.parseDouble(deptAssessment.getTotalScore());
            }
            catch (NumberFormatException e)
            {
                avgScore = 0.0;
            }
        }

        // 计算四级评价占比（从教师和学生考核数据UNION ALL）
        Map<String, Double> ratingDistribution = calculateRatingDistribution(orgCode, year);

        return new CurrentMetricsDTO(
                avgScore,
                ratingDistribution.get("excellent"),
                ratingDistribution.get("good") - ratingDistribution.get("excellent"),
                ratingDistribution.get("pass") - ratingDistribution.get("excellent") - ratingDistribution.get("good")
        );
    }

    /**
     * 计算四级评价占比
     */
    private Map<String, Double> calculateRatingDistribution(String unitId, String year)
    {
        // 查询教师考核评级分布
        List<Map<String, Object>> teacherRatings = teacherAssessmentMapper.selectRatingDistribution(unitId, year);

        // 查询学生考核评级分布
        List<Map<String, Object>> studentRatings = studentAssessmentMapper.selectRatingDistribution(unitId, year);

        // 合并统计
        Map<String, Integer> ratingCounts = new HashMap<>();
        int total = 0;

        // 统计教师评级
        for (Map<String, Object> item : teacherRatings)
        {
            String rating = (String) item.get("rating");
            Long count = ((Number) item.get("count")).longValue();
            ratingCounts.put(rating, ratingCounts.getOrDefault(rating, 0) + count.intValue());
            total += count.intValue();
        }

        // 统计学生评级
        for (Map<String, Object> item : studentRatings)
        {
            String rating = (String) item.get("rating");
            Long count = ((Number) item.get("count")).longValue();
            ratingCounts.put(rating, ratingCounts.getOrDefault(rating, 0) + count.intValue());
            total += count.intValue();
        }

        // 计算比例（累计）
        int excellentCount = ratingCounts.getOrDefault("优秀", 0);
        int goodCount = ratingCounts.getOrDefault("良好", 0);
        int passCount = ratingCounts.getOrDefault("及格", 0);

        double excellentRate = total > 0 ? (double) excellentCount / total : 0.0;
        double goodRate = total > 0 ? (double) (excellentCount + goodCount) / total : 0.0;
        double passRate = total > 0 ? (double) (excellentCount + goodCount + passCount) / total : 0.0;

        Map<String, Double> result = new HashMap<>();
        result.put("excellent", excellentRate);
        result.put("good", goodRate);
        result.put("pass", passRate);

        return result;
    }

    /**
     * 获取下级单位对比数据
     */
    private List<ChildComparisonDTO> getChildrenComparisons(Long parentDeptId, String year)
    {
        // 查询直接下一级部门（通过 parentId 查询）
        SysDept queryDept = new SysDept();
        queryDept.setParentId(parentDeptId);
        queryDept.setStatus("0"); // 只查询正常状态的部门

        List<SysDept> childrenDepts = deptMapper.selectDeptList(queryDept);

        if (childrenDepts == null || childrenDepts.isEmpty())
        {
            return new ArrayList<>();
        }

        // 限制最多80个下级单位（避免数据过多）
        List<SysDept> limitedChildren = childrenDepts.stream()
                .limit(80)
                .collect(Collectors.toList());

        List<ChildComparisonDTO> comparisons = new ArrayList<>();

        for (SysDept childDept : limitedChildren)
        {
            String childOrgCode = childDept.getOrgCode();
            if (childOrgCode == null || childOrgCode.isEmpty())
            {
                continue;
            }

            // 查询该单位的考核数据
            SmsDeptAssessment assessment = deptAssessmentMapper.selectByPersonIdAndYear(childOrgCode, year);

            Double avgScore = 0.0;
            if (assessment != null && assessment.getTotalScore() != null)
            {
                try
                {
                    avgScore = Double.parseDouble(assessment.getTotalScore());
                }
                catch (NumberFormatException e)
                {
                    avgScore = 0.0;
                }
            }

            // 计算该单位的四级评价占比
            Map<String, Double> ratingDistribution = calculateRatingDistribution(childOrgCode, year);

            ChildComparisonDTO comparison = new ChildComparisonDTO(
                    childDept.getDeptName(),
                    avgScore,
                    ratingDistribution.get("excellent"),
                    ratingDistribution.get("good") - ratingDistribution.get("excellent"),
                    ratingDistribution.get("pass") - ratingDistribution.get("excellent") - ratingDistribution.get("good")
            );

            comparisons.add(comparison);
        }

        return comparisons;
    }

    /**
     * 获取年度趋势数据
     */
    private YearlyTrendDTO getYearlyTrend(String orgCode, String year)
    {
        // 生成最近5年的年份列表
        int currentYear = Integer.parseInt(year);
        List<String> years = new ArrayList<>();
        for (int i = 4; i >= 0; i--)
        {
            years.add(String.valueOf(currentYear - i));
        }

        // 查询最近5年的考核数据
        List<SmsDeptAssessment> assessments = deptAssessmentMapper.selectYearlyTrendByPersonId(orgCode, years);

        // 构建年份到考核数据的映射
        Map<String, SmsDeptAssessment> yearMap = new HashMap<>();
        for (SmsDeptAssessment assessment : assessments)
        {
            if (assessment.getPeriod() != null && assessment.getPeriod().length() >= 4)
            {
                String assessmentYear = assessment.getPeriod().substring(0, 4);
                yearMap.put(assessmentYear, assessment);
            }
        }

        // 构建趋势数据
        List<Double> avgScores = new ArrayList<>();
        List<Double> excellentRates = new ArrayList<>();
        List<Double> goodRates = new ArrayList<>();
        List<Double> passRates = new ArrayList<>();
        List<Double> failRates = new ArrayList<>();

        for (String y : years)
        {
            SmsDeptAssessment assessment = yearMap.get(y);

            if (assessment != null && assessment.getTotalScore() != null)
            {
                try
                {
                    avgScores.add(Double.parseDouble(assessment.getTotalScore()));
                }
                catch (NumberFormatException e)
                {
                    avgScores.add(0.0);
                }

                // 获取该年度的评级分布
                Map<String, Double> distribution = calculateRatingDistribution(orgCode, y);

                // 转换为百分比形式
                excellentRates.add(distribution.get("excellent") * 100);
                goodRates.add(distribution.get("good") * 100);
                passRates.add(distribution.get("pass") * 100);
                failRates.add((1 - distribution.get("pass")) * 100);
            }
            else
            {
                // 没有数据时填充0
                avgScores.add(0.0);
                excellentRates.add(0.0);
                goodRates.add(0.0);
                passRates.add(0.0);
                failRates.add(0.0);
            }
        }

        return new YearlyTrendDTO(years, avgScores, excellentRates, goodRates, passRates, failRates);
    }

    /**
     * 创建空的统计数据
     */
    private DashboardStatisticsDTO createEmptyStatistics()
    {
        CurrentMetricsDTO emptyMetrics = new CurrentMetricsDTO(0.0, 0.0, 0.0, 0.0);
        List<ChildComparisonDTO> emptyComparisons = new ArrayList<>();

        // 生成空的年度趋势
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = 4; i >= 0; i--)
        {
            years.add(String.valueOf(currentYear - i));
        }

        List<Double> zeros = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0);
        YearlyTrendDTO emptyTrend = new YearlyTrendDTO(years, zeros, zeros, zeros, zeros, zeros);

        return new DashboardStatisticsDTO(emptyMetrics, emptyComparisons, emptyTrend);
    }
}
