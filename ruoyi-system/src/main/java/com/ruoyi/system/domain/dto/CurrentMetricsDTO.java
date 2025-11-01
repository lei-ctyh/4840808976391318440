package com.ruoyi.system.domain.dto;

/**
 * 当前单位指标 DTO
 *
 * @author ruoyi
 */
public class CurrentMetricsDTO
{
    /** 综合成绩（平均分） */
    private Double avgScore;

    /** 优秀率（小数形式，0-1） */
    private Double excellentRate;

    /** 良好率（小数形式，累计，包含优秀） */
    private Double goodRate;

    /** 及格率（小数形式，累计，包含优秀+良好） */
    private Double passRate;

    public CurrentMetricsDTO()
    {
    }

    public CurrentMetricsDTO(Double avgScore, Double excellentRate, Double goodRate, Double passRate)
    {
        this.avgScore = avgScore;
        this.excellentRate = excellentRate;
        this.goodRate = goodRate;
        this.passRate = passRate;
    }

    public Double getAvgScore()
    {
        return avgScore;
    }

    public void setAvgScore(Double avgScore)
    {
        this.avgScore = avgScore;
    }

    public Double getExcellentRate()
    {
        return excellentRate;
    }

    public void setExcellentRate(Double excellentRate)
    {
        this.excellentRate = excellentRate;
    }

    public Double getGoodRate()
    {
        return goodRate;
    }

    public void setGoodRate(Double goodRate)
    {
        this.goodRate = goodRate;
    }

    public Double getPassRate()
    {
        return passRate;
    }

    public void setPassRate(Double passRate)
    {
        this.passRate = passRate;
    }
}
