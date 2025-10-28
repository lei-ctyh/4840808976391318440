package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 考试项管理对象 sms_exam_item
 * 
 * @author ruoyi
 * @date 2025-10-28
 */
public class SmsExamItem extends TreeEntity
{
    private static final long serialVersionUID = 1L;

    /** 考试项ID */
    private Long itemId;

    /** 考试项名称 */
    @Excel(name = "考试项名称")
    private String itemName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 核定方式（1百分制 2合格制 3取均值） */
    @Excel(name = "核定方式", readConverterExp = "1=百分制,2=合格制,3=取均值")
    private String scoringMethod;

    /** 权重 */
    @Excel(name = "权重")
    private BigDecimal weightPercent;

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setScoringMethod(String scoringMethod) 
    {
        this.scoringMethod = scoringMethod;
    }

    public String getScoringMethod() 
    {
        return scoringMethod;
    }

    public void setWeightPercent(BigDecimal weightPercent) 
    {
        this.weightPercent = weightPercent;
    }

    public BigDecimal getWeightPercent() 
    {
        return weightPercent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("parentId", getParentId())
            .append("itemName", getItemName())
            .append("orderNum", getOrderNum())
            .append("status", getStatus())
            .append("scoringMethod", getScoringMethod())
            .append("remark", getRemark())
            .append("weightPercent", getWeightPercent())
            .toString();
    }
}
