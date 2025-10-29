package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 模板绑定对象 sms_template
 */
public class SmsTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long templateId;

    /** 机构编码 */
    @Excel(name = "机构编码")
    private String orgCode;

    /** 看板类型（leader/student/teacher/org） */
    @Excel(name = "看板类型")
    private String boardType;

    /** 年度（如 2025） */
    @Excel(name = "年度")
    private Integer year;

    /** 文件原始名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 文件服务器相对路径/URL */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件后缀（xls/xlsx） */
    private String fileExt;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getBoardType() { return boardType; }
    public void setBoardType(String boardType) { this.boardType = boardType; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileExt() { return fileExt; }
    public void setFileExt(String fileExt) { this.fileExt = fileExt; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String getRemark() { return remark; }
    @Override
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("orgCode", getOrgCode())
            .append("boardType", getBoardType())
            .append("year", getYear())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileExt", getFileExt())
            .append("fileSize", getFileSize())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}