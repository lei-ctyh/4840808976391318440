package com.ruoyi.system.domain.dto;

/**
 * 下级单位对比数据 DTO
 *
 * @author ruoyi
 */
public class ChildComparisonDTO
{
    /** 下级单位名称 */
    private String name;

    /** 该单位综合成绩 */
    private Double avgScore;

    /** 该单位优秀率（小数） */
    private Double excellentRate;

    /** 该单位良好率（小数，累计） */
    private Double goodRate;

    /** 该单位及格率（小数，累计） */
    private Double passRate;

    public ChildComparisonDTO()
    {
    }

    public ChildComparisonDTO(String name, Double avgScore, Double excellentRate, Double goodRate, Double passRate)
    {
        this.name = name;
        this.avgScore = avgScore;
        this.excellentRate = excellentRate;
        this.goodRate = goodRate;
        this.passRate = passRate;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
