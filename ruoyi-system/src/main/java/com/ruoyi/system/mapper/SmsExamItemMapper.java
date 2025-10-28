package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SmsExamItem;

/**
 * 考试项管理Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-28
 */
public interface SmsExamItemMapper 
{
    /**
     * 查询考试项管理
     * 
     * @param itemId 考试项管理主键
     * @return 考试项管理
     */
    public SmsExamItem selectSmsExamItemByItemId(Long itemId);

    /**
     * 查询考试项管理列表
     * 
     * @param smsExamItem 考试项管理
     * @return 考试项管理集合
     */
    public List<SmsExamItem> selectSmsExamItemList(SmsExamItem smsExamItem);

    /**
     * 新增考试项管理
     * 
     * @param smsExamItem 考试项管理
     * @return 结果
     */
    public int insertSmsExamItem(SmsExamItem smsExamItem);

    /**
     * 修改考试项管理
     * 
     * @param smsExamItem 考试项管理
     * @return 结果
     */
    public int updateSmsExamItem(SmsExamItem smsExamItem);

    /**
     * 删除考试项管理
     * 
     * @param itemId 考试项管理主键
     * @return 结果
     */
    public int deleteSmsExamItemByItemId(Long itemId);

    /**
     * 批量删除考试项管理
     * 
     * @param itemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSmsExamItemByItemIds(Long[] itemIds);
}
