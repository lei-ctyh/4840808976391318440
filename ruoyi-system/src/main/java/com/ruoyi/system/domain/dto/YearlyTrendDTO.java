package com.ruoyi.system.domain.dto;

import java.util.List;

/**
 * 年度趋势数据 DTO
 *
 * @author ruoyi
 */
public class YearlyTrendDTO
{
    /** 年份数组（5个元素） */
    private List<String> years;

    /** 对应年份的综合成绩 */
    private List<Double> avgScores;

    /** 对应年份的优秀率（百分比形式，0-100） */
    private List<Double> excellentRates;

    /** 对应年份的良好率（百分比形式，累计） */
    private List<Double> goodRates;

    /** 对应年份的及格率（百分比形式，累计） */
    private List<Double> passRates;

    /** 对应年份的未及格率（百分比形式） */
    private List<Double> failRates;

    public YearlyTrendDTO()
    {
    }

    public YearlyTrendDTO(List<String> years, List<Double> avgScores, List<Double> excellentRates,
                          List<Double> goodRates, List<Double> passRates, List<Double> failRates)
    {
        this.years = years;
        this.avgScores = avgScores;
        this.excellentRates = excellentRates;
        this.goodRates = goodRates;
        this.passRates = passRates;
        this.failRates = failRates;
    }

    public List<String> getYears()
    {
        return years;
    }

    public void setYears(List<String> years)
    {
        this.years = years;
    }

    public List<Double> getAvgScores()
    {
        return avgScores;
    }

    public void setAvgScores(List<Double> avgScores)
    {
        this.avgScores = avgScores;
    }

    public List<Double> getExcellentRates()
    {
        return excellentRates;
    }

    public void setExcellentRates(List<Double> excellentRates)
    {
        this.excellentRates = excellentRates;
    }

    public List<Double> getGoodRates()
    {
        return goodRates;
    }

    public void setGoodRates(List<Double> goodRates)
    {
        this.goodRates = goodRates;
    }

    public List<Double> getPassRates()
    {
        return passRates;
    }

    public void setPassRates(List<Double> passRates)
    {
        this.passRates = passRates;
    }

    public List<Double> getFailRates()
    {
        return failRates;
    }

    public void setFailRates(List<Double> failRates)
    {
        this.failRates = failRates;
    }
}
