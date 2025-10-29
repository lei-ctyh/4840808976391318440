package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SmsTemplate;
import com.ruoyi.system.mapper.SmsTemplateMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.ISmsTemplateService;
import com.ruoyi.common.core.domain.entity.SysDept;

/**
 * 模板绑定Service业务层处理
 */
@Service
public class SmsTemplateServiceImpl implements ISmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public int bindTemplate(SmsTemplate template) {
        SmsTemplate exists = smsTemplateMapper.selectByUnique(template.getOrgCode(), template.getBoardType(), template.getYear());
        String username = null;
        try { username = SecurityUtils.getUsername(); } catch (Exception ignored) {}
        if (exists == null) {
            template.setCreateBy(username);
            template.setCreateTime(DateUtils.getNowDate());
            template.setUpdateBy(username);
            template.setUpdateTime(DateUtils.getNowDate());
            return smsTemplateMapper.insertSmsTemplate(template);
        } else {
            exists.setFileName(template.getFileName());
            exists.setFilePath(template.getFilePath());
            exists.setFileExt(template.getFileExt());
            exists.setFileSize(template.getFileSize());
            exists.setStatus(template.getStatus());
            exists.setRemark(template.getRemark());
            exists.setUpdateBy(username);
            exists.setUpdateTime(DateUtils.getNowDate());
            return smsTemplateMapper.updateSmsTemplate(exists);
        }
    }

    @Override
    public SmsTemplate resolveTemplate(String orgCode, String boardType, Integer year) {
        if (orgCode == null || year == null || boardType == null) {
            return null;
        }
        String currentOrgCode = orgCode;
        int maxDepth = 100; // 防御性限制，避免异常环
        while (currentOrgCode != null && maxDepth-- > 0) {
            // 先尝试直接查找当前机构编码的模板
            SmsTemplate hit = smsTemplateMapper.selectByUnique(currentOrgCode, boardType, year);
            if (hit != null) {
                return hit;
            }
            // 根据机构编码查找机构信息
            SysDept dept = sysDeptMapper.selectDeptByOrgCode(currentOrgCode);
            if (dept == null) break;
            
            // 获取父机构ID，然后查找父机构的机构编码
            Long parentId = dept.getParentId();
            if (parentId == null || parentId == 0L) {
                break;
            }
            SysDept parentDept = sysDeptMapper.selectDeptById(parentId);
            if (parentDept == null || parentDept.getOrgCode() == null || parentDept.getOrgCode().equals(currentOrgCode)) {
                break;
            }
            currentOrgCode = parentDept.getOrgCode();
        }
        return null;
    }

    @Override
    public boolean exists(String orgCode, String boardType, Integer year) {
        return smsTemplateMapper.selectByUnique(orgCode, boardType, year) != null;
    }
}