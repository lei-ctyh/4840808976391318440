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
import com.ruoyi.system.domain.SmsTeacherAssessment;
import com.ruoyi.system.mapper.SmsTeacherAssessmentMapper;
import com.ruoyi.system.service.ISmsTeacherAssessmentService;

/**
 * 教师考核Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-10
 */
@Service
public class SmsTeacherAssessmentServiceImpl implements ISmsTeacherAssessmentService {
    private static final Logger log = LoggerFactory.getLogger(SmsTeacherAssessmentServiceImpl.class);

    @Autowired
    private SmsTeacherAssessmentMapper smsTeacherAssessmentMapper;

    /**
     * 查询教师考核列表
     *
     * @param smsTeacherAssessment 教师考核
     * @return 教师考核
     */
    @Override
    public List<SmsTeacherAssessment> selectSmsTeacherAssessmentList(SmsTeacherAssessment smsTeacherAssessment) {
        return smsTeacherAssessmentMapper.selectSmsTeacherAssessmentList(smsTeacherAssessment);
    }

    /**
     * 根据人员ID和考核周期查询考核记录
     *
     * @param personId 人员ID
     * @param period   考核周期
     * @return 考核记录
     */
    @Override
    public SmsTeacherAssessment selectByPersonIdAndPeriod(String personId, String period) {
        return smsTeacherAssessmentMapper.selectByPersonIdAndPeriod(personId, period);
    }

    /**
     * 根据单位ID和考核周期查询考核记录列表
     *
     * @param unitId 单位ID
     * @param period 考核周期
     * @return 考核记录列表
     */
    @Override
    public List<SmsTeacherAssessment> selectByUnitIdAndPeriod(String unitId, String period) {
        return smsTeacherAssessmentMapper.selectByUnitIdAndPeriod(unitId, period);
    }

    /**
     * 导入教师考核数据
     *
     * @param file          Excel文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    @Override
    public AjaxResult importTeacherAssessment(MultipartFile file, boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        try (InputStream is = file.getInputStream()) {
            Workbook workbook;
            String fileName = file.getOriginalFilename();
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is);
            } else {
                return AjaxResult.error("不支持的文件格式，请上传Excel文件");
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return AjaxResult.error("Excel文件中没有找到工作表");
            }

            // 解析表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return AjaxResult.error("Excel文件表头不能为空");
            }

            Map<String, Integer> fieldMapping = parseHeader(headerRow);
            validateRequiredFields(fieldMapping);

            List<SmsTeacherAssessment> assessmentList = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            int successCount = 0;
            int updateCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            // 从第二行开始解析数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                try {
                    SmsTeacherAssessment assessment = parseRowData(row, fieldMapping);
                    String validationError = validateAssessmentData(assessment);
                    if (StringUtils.isNotEmpty(validationError)) {
                        errorMessages.add("第" + (i + 1) + "行：" + validationError);
                        errorCount++;
                        continue;
                    }

                    // 检查是否已存在
                    SmsTeacherAssessment existing = smsTeacherAssessmentMapper.selectByPersonIdAndPeriod(
                            assessment.getPersonId(), assessment.getPeriod());

                    if (existing != null) {
                        if (updateSupport) {
                            // 更新现有记录
                            assessment.setCreateBy(existing.getCreateBy());
                            assessment.setCreateTime(existing.getCreateTime());
                            assessment.setUpdateBy("admin"); // 可以从当前用户获取
                            assessment.setUpdateTime(DateUtils.getNowDate());
                            smsTeacherAssessmentMapper.updateSmsTeacherAssessment(assessment);
                            updateCount++;
                        } else {
                            errorMessages.add("第" + (i + 1) + "行：人员编号 " + assessment.getPersonId() +
                                    " 在周期 " + assessment.getPeriod() + " 的记录已存在");
                            errorCount++;
                        }
                    } else {
                        // 新增记录
                        assessment.setCreateBy("admin"); // 可以从当前用户获取
                        assessment.setCreateTime(DateUtils.getNowDate());
                        assessment.setUpdateBy("admin");
                        assessment.setUpdateTime(DateUtils.getNowDate());
                        assessmentList.add(assessment);
                    }
                } catch (Exception e) {
                    log.error("导入第" + (i + 1) + "行数据失败", e);
                    errorMessages.add("第" + (i + 1) + "行：" + e.getMessage());
                    errorCount++;
                }
            }

            // 批量插入新记录
            if (!assessmentList.isEmpty()) {
                smsTeacherAssessmentMapper.batchInsertSmsTeacherAssessment(assessmentList);
                successCount += assessmentList.size();
            }

            workbook.close();

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", successCount + updateCount + skipCount + errorCount);
            result.put("success", successCount);
            result.put("update", updateCount);
            result.put("skip", skipCount);
            result.put("error", errorCount);
            result.put("errorMessages", errorMessages);

            return AjaxResult.success("导入完成", result);

        } catch (Exception e) {
            log.error("导入教师考核数据失败", e);
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 解析Excel表头
     */
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

    /**
     * 将表头名称映射到字段名
     */
    private String mapHeaderToField(String headerName, int columnIndex) {
        Map<String, String> headerMapping = new HashMap<>();
        headerMapping.put("人员编号", "personId");
        headerMapping.put("姓名", "personName");
        headerMapping.put("单位", "unitId");
        headerMapping.put("出生年月", "birthDate");
        headerMapping.put("年龄", "age");
        headerMapping.put("衔级", "title");
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
            if (columnIndex >= 0) {
                // 格式化为三位数字符串
                String metricIndex = String.format("%03d", columnIndex+1);
                result = "metric" + metricIndex;
            }
        }

        return result;
    }

    /**
     * 验证必需字段
     */
    private void validateRequiredFields(Map<String, Integer> fieldMapping) throws Exception {
        String[] requiredFields = {"personId", "personName", "period"};
        for (String field : requiredFields) {
            if (!fieldMapping.containsKey(field)) {
                throw new Exception("缺少必需的表头：" + getFieldDescription(field));
            }
        }
    }

    /**
     * 获取字段描述
     */
    private String getFieldDescription(String fieldName) {
        switch (fieldName) {
            case "personId":
                return "人员编号";
            case "personName":
                return "姓名";
            case "unitId":
                return "单位编号";
            case "birthDate":
                return "出生年月";
            case "age":
                return "年龄";
            case "title":
                return "衔级";
            case "period":
                return "评定周期";
            default:
                return fieldName;
        }
    }

    /**
     * 解析行数据
     */
    private SmsTeacherAssessment parseRowData(Row row, Map<String, Integer> fieldMapping) throws Exception {
        SmsTeacherAssessment assessment = new SmsTeacherAssessment();

        for (Map.Entry<String, Integer> entry : fieldMapping.entrySet()) {
            String fieldName = entry.getKey();
            Integer columnIndex = entry.getValue();
            Cell cell = row.getCell(columnIndex);
            String cellValue = getCellValue(cell);

            setFieldValue(assessment, fieldName, cellValue);
        }

        return assessment;
    }

    /**
     * 设置字段值
     */
    private void setFieldValue(SmsTeacherAssessment assessment, String fieldName, String cellValue) throws Exception {
        if (StringUtils.isEmpty(cellValue)) {
            return;
        }

        switch (fieldName) {
            case "personId":
                assessment.setPersonId(cellValue);
                break;
            case "personName":
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
            default:
                // 处理metric字段
                if (fieldName.startsWith("metric")) {
                    // 使用反射设置metric字段值
                    try {
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        assessment.getClass().getMethod(methodName, String.class).invoke(assessment, cellValue);
                    } catch (Exception e) {
                        log.warn("设置字段值失败：" + fieldName + " = " + cellValue, e);
                    }
                }
                break;
        }
    }

    /**
     * 解析日期字符串
     */
    private Date parseDateString(String dateStr) throws Exception {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }

        SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM"),
                new SimpleDateFormat("yyyy/MM"),
                new SimpleDateFormat("yyyy.MM"),
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("yyyy/MM/dd"),
                new SimpleDateFormat("yyyy.MM.dd")
        };

        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(dateStr);
            } catch (Exception e) {
                // 继续尝试下一个格式
            }
        }

        throw new Exception("无法解析日期格式：" + dateStr);
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    // 处理数字，避免科学计数法
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
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 判断是否为空行
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotEmpty(getCellValue(cell))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证考核数据
     */
    private String validateAssessmentData(SmsTeacherAssessment assessment) {
        if (StringUtils.isEmpty(assessment.getPersonId())) {
            return "人员编号不能为空";
        }
        if (StringUtils.isEmpty(assessment.getPersonName())) {
            return "姓名不能为空";
        }
        if (StringUtils.isEmpty(assessment.getPeriod())) {
            return "评定周期不能为空";
        }
        if (StringUtils.isNotEmpty(assessment.getUnitId()) && !isValidUnitId(assessment.getUnitId())) {
            return "单位编号格式不正确";
        }
        return null;
    }

    /**
     * 验证单位编号格式
     */
    private boolean isValidUnitId(String unitId) {
        // 根据机构编码规则验证
        return Pattern.matches("^(00|01)\\d*$", unitId);
    }
}