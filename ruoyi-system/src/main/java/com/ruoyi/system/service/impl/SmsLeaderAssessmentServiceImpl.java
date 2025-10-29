package com.ruoyi.system.service.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SmsLeaderAssessment;
import com.ruoyi.system.mapper.SmsLeaderAssessmentMapper;
import com.ruoyi.system.service.ISmsLeaderAssessmentService;

/**
 * 领导班子考核Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
@Service
public class SmsLeaderAssessmentServiceImpl implements ISmsLeaderAssessmentService 
{
    private static final Logger log = LoggerFactory.getLogger(SmsLeaderAssessmentServiceImpl.class);

    @Autowired
    private SmsLeaderAssessmentMapper smsLeaderAssessmentMapper;

    /**
     * 查询领导班子考核列表
     * 
     * @param smsLeaderAssessment 领导班子考核
     * @return 领导班子考核
     */
    @Override
    public List<SmsLeaderAssessment> selectSmsLeaderAssessmentList(SmsLeaderAssessment smsLeaderAssessment)
    {
        return smsLeaderAssessmentMapper.selectSmsLeaderAssessmentList(smsLeaderAssessment);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     * 
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    @Override
    public SmsLeaderAssessment selectByPersonIdAndPeriod(String personId, String period)
    {
        return smsLeaderAssessmentMapper.selectByPersonIdAndPeriod(personId, period);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    @Override
    public List<SmsLeaderAssessment> selectByUnitIdAndPeriod(String unitId, String period)
    {
        return smsLeaderAssessmentMapper.selectByUnitIdAndPeriod(unitId, period);
    }

    /**
     * 导入领导班子考核数据
     * 
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    @Override
    public AjaxResult importLeaderAssessment(MultipartFile file, boolean updateSupport) throws Exception
    {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            return AjaxResult.error("请上传Excel格式文件");
        }

        List<SmsLeaderAssessment> assessmentList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook;
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return AjaxResult.error("Excel文件中没有数据");
            }

            // 解析表头
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> fieldMapping = parseHeader(headerRow);
            validateRequiredFields(fieldMapping);

            // 解析数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                try {
                    SmsLeaderAssessment assessment = parseRowData(row, fieldMapping);
                    String validationError = validateAssessmentData(assessment);
                    if (StringUtils.isNotEmpty(validationError)) {
                        errorMessages.add("第" + (i + 1) + "行：" + validationError);
                        errorCount++;
                        continue;
                    }

                    // 检查是否已存在
                    SmsLeaderAssessment existing = smsLeaderAssessmentMapper.selectByPersonIdAndPeriod(
                        assessment.getPersonId(), assessment.getPeriod());
                    
                    if (existing != null) {
                        if (updateSupport) {
                            assessment.setUpdateTime(DateUtils.getNowDate());
                            smsLeaderAssessmentMapper.updateSmsLeaderAssessment(assessment);
                            updateCount++;
                        } else {
                            skipCount++;
                        }
                    } else {
                        assessment.setCreateTime(DateUtils.getNowDate());
                        smsLeaderAssessmentMapper.insertSmsLeaderAssessment(assessment);
                        successCount++;
                    }
                } catch (Exception e) {
                    errorMessages.add("第" + (i + 1) + "行：" + e.getMessage());
                    errorCount++;
                }
            }

            workbook.close();
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", successCount + updateCount + skipCount + errorCount);
        result.put("success", successCount);
        result.put("update", updateCount);
        result.put("skip", skipCount);
        result.put("error", errorCount);
        result.put("errorMessages", errorMessages);

        return AjaxResult.success("导入完成", result);
    }



    // 以下是辅助方法，保持不变
    private Map<String, Integer> parseHeader(Row headerRow) {
        Map<String, Integer> fieldMapping = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerName = getCellValue(cell).trim();
                String fieldName = mapHeaderToField(headerName);
                if (StringUtils.isNotEmpty(fieldName)) {
                    fieldMapping.put(fieldName, i);
                }
            }
        }
        return fieldMapping;
    }

    private String mapHeaderToField(String headerName) {
        Map<String, String> headerMapping = new HashMap<>();
        headerMapping.put("人员编号", "personId");
        headerMapping.put("姓名", "personName");
        headerMapping.put("单位", "unitId");
        headerMapping.put("出生年月", "birthDate");
        headerMapping.put("年龄", "age");
        headerMapping.put("职称", "title");
        headerMapping.put("评定周期", "period");
        headerMapping.put("总成绩", "totalScore");
        headerMapping.put("综合成绩", "totalScore");
        headerMapping.put("总评定", "totalRating");
        headerMapping.put("四级制", "totalRating");
        headerMapping.put("备注", "remark");
        headerMapping.put("状态", "status");
        
        // 检查是否是metric字段 (metric001 到 metric100)
        String result = headerMapping.get(headerName);
        if (result == null) {
            // 如果是数字编号的字段，映射到对应的metric字段
            if (headerName.matches("\\d{3}")) {
                result = "metric" + headerName;
            }
        }
        
        return result;
    }

    private void validateRequiredFields(Map<String, Integer> fieldMapping) throws Exception {
        String[] requiredFields = {"personId", "personName", "unitId", "period"};
        List<String> missingFields = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (!fieldMapping.containsKey(field)) {
                missingFields.add(getFieldDescription(field));
            }
        }
        
        if (!missingFields.isEmpty()) {
            throw new Exception("缺少必要的列：" + String.join("、", missingFields));
        }
    }

    private String getFieldDescription(String fieldName) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("personId", "人员编号");
        descriptions.put("personName", "姓名");
        descriptions.put("unitId", "单位");
        descriptions.put("period", "评定周期");
        descriptions.put("totalScore", "总成绩");
        descriptions.put("totalRating", "总评定");
        descriptions.put("remark", "备注");
        descriptions.put("status", "状态");
        return descriptions.getOrDefault(fieldName, fieldName);
    }

    private SmsLeaderAssessment parseRowData(Row row, Map<String, Integer> fieldMapping) throws Exception {
        SmsLeaderAssessment assessment = new SmsLeaderAssessment();
        
        for (Map.Entry<String, Integer> entry : fieldMapping.entrySet()) {
            String fieldName = entry.getKey();
            int columnIndex = entry.getValue();
            Cell cell = row.getCell(columnIndex);
            String cellValue = getCellValue(cell);
            
            if (StringUtils.isNotEmpty(cellValue)) {
                setFieldValue(assessment, fieldName, cellValue);
            }
        }
        
        return assessment;
    }

    private void setFieldValue(SmsLeaderAssessment assessment, String fieldName, String cellValue) throws Exception {
        switch (fieldName) {
            case "personId":
                assessment.setPersonId(cellValue);
                break;
            case "personName":
            case "name":
                assessment.setPersonName(cellValue);
                break;
            case "unitId":
                assessment.setUnitId(cellValue);
                break;
            case "birthDate":
                assessment.setBirthDate(cellValue);
                break;
            case "age":
                assessment.setAge(cellValue);
                break;
            case "title":
                assessment.setTitle(cellValue);
                break;
            case "period":
                assessment.setPeriod(cellValue);
                break;
            case "totalScore":
                assessment.setTotalScore(cellValue);
                break;
            case "totalRating":
                assessment.setTotalRating(cellValue);
                break;
            case "remark":
                assessment.setRemark(cellValue);
                break;
            case "status":
                assessment.setStatus(cellValue);
                break;
            // 处理metric字段 (metric001 到 metric100)
            default:
                if (fieldName.startsWith("metric") && fieldName.length() == 9) {
                    // 使用反射设置metric字段
                    try {
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        java.lang.reflect.Method method = assessment.getClass().getMethod(methodName, String.class);
                        method.invoke(assessment, cellValue);
                    } catch (Exception e) {
                        log.warn("无法设置字段 {}: {}", fieldName, e.getMessage());
                    }
                }
                break;
        }
    }

    private Date parseDateString(String dateStr) throws Exception {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        
        SimpleDateFormat[] formats = {
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy-MM"),
            new SimpleDateFormat("yyyy/MM")
        };
        
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(dateStr);
            } catch (Exception e) {
                // 继续尝试下一个格式
            }
        }
        
        throw new Exception("日期格式不正确：" + dateStr);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotEmpty(getCellValue(cell))) {
                return false;
            }
        }
        return true;
    }

    private String validateAssessmentData(SmsLeaderAssessment assessment) {
        if (StringUtils.isEmpty(assessment.getPersonId())) {
            return "人员编号不能为空";
        }
        if (StringUtils.isEmpty(assessment.getPersonName())) {
            return "姓名不能为空";
        }
        if (StringUtils.isEmpty(assessment.getUnitId())) {
            return "单位不能为空";
        }
        if (StringUtils.isEmpty(assessment.getPeriod())) {
            return "评定周期不能为空";
        }
        if (!isValidUnitId(assessment.getUnitId())) {
            return "单位编码格式不正确";
        }
        return null;
    }

    private boolean isValidUnitId(String unitId) {
        if (StringUtils.isEmpty(unitId)) {
            return false;
        }
        // 验证单位编码格式
        Pattern pattern = Pattern.compile("^(00|01)\\d{2,6}$");
        return pattern.matcher(unitId).matches();
    }
}