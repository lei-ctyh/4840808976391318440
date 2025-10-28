package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SmsExamItemMapper;
import com.ruoyi.system.domain.SmsExamItem;
import com.ruoyi.system.service.ISmsExamItemService;

/**
 * 考试项管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class SmsExamItemServiceImpl implements ISmsExamItemService 
{
    @Autowired
    private SmsExamItemMapper smsExamItemMapper;

    /**
     * 查询考试项管理
     * 
     * @param itemId 考试项管理主键
     * @return 考试项管理
     */
    @Override
    public SmsExamItem selectSmsExamItemByItemId(Long itemId)
    {
        return smsExamItemMapper.selectSmsExamItemByItemId(itemId);
    }

    /**
     * 查询考试项管理列表
     * 
     * @param smsExamItem 考试项管理
     * @return 考试项管理
     */
    @Override
    public List<SmsExamItem> selectSmsExamItemList(SmsExamItem smsExamItem)
    {
        return smsExamItemMapper.selectSmsExamItemList(smsExamItem);
    }

    /**
     * 新增考试项管理
     * 
     * @param smsExamItem 考试项管理
     * @return 结果
     */
    @Override
    public int insertSmsExamItem(SmsExamItem smsExamItem)
    {
        return smsExamItemMapper.insertSmsExamItem(smsExamItem);
    }

    /**
     * 修改考试项管理
     * 
     * @param smsExamItem 考试项管理
     * @return 结果
     */
    @Override
    public int updateSmsExamItem(SmsExamItem smsExamItem)
    {
        return smsExamItemMapper.updateSmsExamItem(smsExamItem);
    }

    /**
     * 批量删除考试项管理
     * 
     * @param itemIds 需要删除的考试项管理主键
     * @return 结果
     */
    @Override
    public int deleteSmsExamItemByItemIds(Long[] itemIds)
    {
        return smsExamItemMapper.deleteSmsExamItemByItemIds(itemIds);
    }

    /**
     * 删除考试项管理信息
     * 
     * @param itemId 考试项管理主键
     * @return 结果
     */
    @Override
    public int deleteSmsExamItemByItemId(Long itemId)
    {
        return smsExamItemMapper.deleteSmsExamItemByItemId(itemId);
    }
}
