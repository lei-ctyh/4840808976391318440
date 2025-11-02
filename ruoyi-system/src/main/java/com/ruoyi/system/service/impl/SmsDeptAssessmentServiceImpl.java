package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SmsDeptAssessment;
import com.ruoyi.system.mapper.SmsDeptAssessmentMapper;
import com.ruoyi.system.service.ISmsDeptAssessmentService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 单位成绩考核Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
@Service
public class SmsDeptAssessmentServiceImpl implements ISmsDeptAssessmentService 
{
    private static final Logger log = LoggerFactory.getLogger(SmsDeptAssessmentServiceImpl.class);

    @Autowired
    private SmsDeptAssessmentMapper smsDeptAssessmentMapper;

    /**
     * 查询单位成绩考核列表
     * 
     * @param smsDeptAssessment 单位成绩考核
     * @return 单位成绩考核
     */
    @Override
    public List<SmsDeptAssessment> selectSmsDeptAssessmentList(SmsDeptAssessment smsDeptAssessment)
    {
        return smsDeptAssessmentMapper.selectSmsDeptAssessmentList(smsDeptAssessment);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     * 
     * @param personId 人员ID
     * @param period 考核周期
     * @return 考核记录
     */
    @Override
    public SmsDeptAssessment selectByPersonIdAndPeriod(String personId, String period)
    {
        return smsDeptAssessmentMapper.selectByPersonIdAndPeriod(personId, period);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     * 
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    @Override
    public List<SmsDeptAssessment> selectByUnitIdAndPeriod(String unitId, String period)
    {
        return smsDeptAssessmentMapper.selectByUnitIdAndPeriod(unitId, period);
    }

    /**
     * 导入单位成绩考核数据
     * 
     * @param file Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    @Override
    public AjaxResult importDeptAssessment(MultipartFile file, boolean updateSupport) throws Exception
    {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            return AjaxResult.error("请上传Excel格式文件");
        }

        List<SmsDeptAssessment> assessmentList = new ArrayList<>();
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
                    SmsDeptAssessment assessment = parseRowData(row, fieldMapping);
                    String validationError = validateAssessmentData(assessment);
                    if (StringUtils.isNotEmpty(validationError)) {
                        errorMessages.add("第" + (i + 1) + "行：" + validationError);
                        errorCount++;
                        continue;
                    }

                    // 检查是否已存在
                    SmsDeptAssessment existing = smsDeptAssessmentMapper.selectByPersonIdAndPeriod(
                        assessment.getPersonId(), assessment.getPeriod());
                    
                    if (existing != null) {
                        if (updateSupport) {
                            assessment.setUpdateTime(DateUtils.getNowDate());
                            smsDeptAssessmentMapper.updateSmsDeptAssessment(assessment);
                            updateCount++;
                        } else {
                            skipCount++;
                        }
                    } else {
                        assessment.setCreateTime(DateUtils.getNowDate());
                        smsDeptAssessmentMapper.insertSmsDeptAssessment(assessment);
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("导入第" + (i + 1) + "行数据失败", e);
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
                String fieldName = mapHeaderToField(headerName, i);
                if (StringUtils.isNotEmpty(fieldName)) {
                    fieldMapping.put(fieldName, i);
                }
            }
        }
        return fieldMapping;
    }

    private String mapHeaderToField(String headerName, int columnIndex) {
        Map<String, String> headerMapping = new HashMap<>();
        // 单位成绩场景：personId 表示“单位编号”
        headerMapping.put("单位编号", "personId");
        headerMapping.put("部门编号", "personId");
        headerMapping.put("单位代码", "personId");
        // unitId 表示“所属单位编号”（上级单位/归属单位）
        headerMapping.put("所属单位编号", "unitId");
        headerMapping.put("所属单位", "unitId");
        headerMapping.put("上级单位编号", "unitId");
        // 评定周期
        headerMapping.put("评定周期", "period");
        headerMapping.put("总成绩", "totalScore");
        headerMapping.put("成绩", "totalScore");
        headerMapping.put("综合成绩", "totalScore");
        headerMapping.put("总评定", "totalRating");
        headerMapping.put("成绩评定", "totalRating");
        headerMapping.put("备注", "remark");
        headerMapping.put("状态", "status");
        
        // 检查是否是metric字段 (metric001 到 metric100)
        String result = headerMapping.get(headerName);
        if (result == null) {
            // 如果是数字编号的字段，映射到对应的metric字段
            if (columnIndex >= 0) {
                // 格式化为三位数字符串
                String metricIndex = String.format("%03d", columnIndex+1);
                result = "metric" + metricIndex;
            }
        }
        
        return result;
    }

    private void validateRequiredFields(Map<String, Integer> fieldMapping) throws Exception {
        // 单位成绩导入的必填列：单位编号(personId)、所属单位编号(unitId)、评定周期(period)
        String[] requiredFields = {"personId", "unitId", "period"};
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
        // 单位成绩场景
        descriptions.put("personId", "单位编号");
        descriptions.put("unitId", "所属单位编号");
        descriptions.put("period", "评定周期");
        descriptions.put("totalScore", "总成绩");
        descriptions.put("totalRating", "总评定");
        descriptions.put("remark", "备注");
        descriptions.put("status", "状态");
        return descriptions.getOrDefault(fieldName, fieldName);
    }

    private SmsDeptAssessment parseRowData(Row row, Map<String, Integer> fieldMapping) throws Exception {
        SmsDeptAssessment assessment = new SmsDeptAssessment();
        
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

    private void setFieldValue(SmsDeptAssessment assessment, String fieldName, String cellValue) throws Exception {
        switch (fieldName) {
            case "personId":
                assessment.setPersonId(cellValue);
                break;
            case "unitId":
                assessment.setUnitId(cellValue);
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

    private String validateAssessmentData(SmsDeptAssessment assessment) {
        if (StringUtils.isEmpty(assessment.getPersonId())) {
            return "考核单位编号不能为空";
        }

        if (StringUtils.isEmpty(assessment.getUnitId())) {
            return "所属单位不能为空";
        }
        if (StringUtils.isEmpty(assessment.getPeriod())) {
            return "评定周期不能为空";
        }
        if (!isValidUnitId(assessment.getUnitId())) {
            return "单位编码格式不正确";
        }
        // 评定周期必须是yyyy格式
        if (!assessment.getPeriod().matches("\\d{4}")) {
            return "评定周期格式不正确";
        }
        return null;
    }

    private boolean isValidUnitId(String unitId) {
        if (StringUtils.isEmpty(unitId)) {
            return false;
        }
        // 验证单位编码格式
        Pattern pattern = Pattern.compile("^(00|01)\\d{0,10}$");
        return pattern.matcher(unitId).matches();
    }
}