package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SmsStudentAssessment;
import com.ruoyi.system.mapper.SmsStudentAssessmentMapper;
import com.ruoyi.system.service.ISmsStudentAssessmentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SmsStudentAssessmentServiceImpl implements ISmsStudentAssessmentService {

    @Resource
    private SmsStudentAssessmentMapper studentMapper;

    @Override
    public List<SmsStudentAssessment> selectSmsStudentAssessmentList(SmsStudentAssessment query) {
        return studentMapper.selectSmsStudentAssessmentList(query);
    }

    @Override
    public SmsStudentAssessment selectByPersonIdAndPeriod(String personId, String period) {
        return studentMapper.selectByPersonIdAndPeriod(personId, period);
    }

    @Override
    public List<SmsStudentAssessment> selectByUnitIdAndPeriod(String unitId, String period) {
        return studentMapper.selectByUnitIdAndPeriod(unitId, period);
    }

    @Override
    public String importStudentAssessment(MultipartFile file, boolean updateSupport) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件为空");
        }
        int insertCount = 0;
        int updateCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new ServiceException("Excel 内容为空");
            }

            Map<Integer, String> headerMap = parseHeader(sheet.getRow(0));
            validateRequiredFields(headerMap);

            List<SmsStudentAssessment> toInsert = new ArrayList<>();
            List<SmsStudentAssessment> toUpdate = new ArrayList<>();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isEmptyRow(row)) { continue; }
                SmsStudentAssessment data = parseRowData(row, headerMap);
                validateAssessmentData(data);

                SmsStudentAssessment existing = studentMapper.selectByPersonIdAndPeriod(data.getPersonId(), data.getPeriod());
                if (existing == null) {
                    data.setCreateBy("admin");
                    data.setCreateTime(DateUtils.getNowDate());
                    data.setUpdateBy("admin");
                    data.setUpdateTime(DateUtils.getNowDate());
                    toInsert.add(data);
                    insertCount++;
                } else if (updateSupport) {
                    data.setUpdateBy("admin");
                    data.setUpdateTime(DateUtils.getNowDate());
                    toUpdate.add(data);
                    updateCount++;
                }
            }

            if (!toInsert.isEmpty()) {
                studentMapper.batchInsertSmsStudentAssessment(toInsert);
            }
            for (SmsStudentAssessment upd : toUpdate) {
                studentMapper.updateSmsStudentAssessment(upd);
            }

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("导入失败：" + e.getMessage(), e);
        }

        return String.format("导入成功：新增 %d 条，更新 %d 条", insertCount, updateCount);
    }

    private Map<Integer, String> parseHeader(Row headerRow) {
        if (headerRow == null) {
            throw new ServiceException("表头为空");
        }
        Map<Integer, String> map = new HashMap<>();
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            Cell cell = headerRow.getCell(c);
            String header = getCellValue(cell);
            if (StringUtils.isNotEmpty(header)) {
                map.put(c, mapHeaderToField(header.trim()));
            }
        }
        return map;
    }

    private String mapHeaderToField(String header) {
        switch (header) {
            case "人员编号": return "personId";
            case "姓名": return "personName";
            case "单位编号":
            case "单位": return "unitId";
            case "出生年月": return "birthDate";
            case "年龄": return "age";
            case "班级": return "className";
            case "评定周期": return "period";
            case "总成绩": return "totalScore";
            case "总评定": return "totalRating";
            case "备注": return "remark";
            case "状态": return "status";
            default:
                if (header.startsWith("metric") && header.length() == 9) {
                    return header; // metric001 - metric100
                }
                // 允许 Excel 标题为 "指标001" 映射到 metric001
                if (header.startsWith("指标") && header.length() == 5) {
                    String idx = header.substring(2);
                    return "metric" + idx;
                }
                return null;
        }
    }

    private void validateRequiredFields(Map<Integer, String> headerMap) {
        Set<String> required = new HashSet<>(Arrays.asList("personId", "personName", "unitId", "period"));
        for (String key : required) {
            if (!headerMap.containsValue(key)) {
                throw new ServiceException("缺少必要字段：" + getFieldDescription(key));
            }
        }
    }

    private String getFieldDescription(String field) {
        switch (field) {
            case "personId": return "人员编号";
            case "personName": return "姓名";
            case "unitId": return "单位编号";
            case "period": return "评定周期";
            default: return field;
        }
    }

    private SmsStudentAssessment parseRowData(Row row, Map<Integer, String> headerMap) {
        SmsStudentAssessment data = new SmsStudentAssessment();
        for (Map.Entry<Integer, String> e : headerMap.entrySet()) {
            String field = e.getValue();
            if (StringUtils.isEmpty(field)) continue;
            String value = getCellValue(row.getCell(e.getKey()));
            setFieldValue(data, field, value);
        }
        return data;
    }

    private void setFieldValue(SmsStudentAssessment d, String field, String value) {
        if (value != null) value = value.trim();
        switch (field) {
            case "personId": d.setPersonId(value); break;
            case "personName": d.setPersonName(value); break;
            case "unitId": d.setUnitId(value); break;
            case "birthDate": d.setBirthDate(parseDateString(value, "yyyy-MM")); break;
            case "age": d.setAge(value); break;
            case "className": d.setClassName(value); break;
            case "period": d.setPeriod(parsePeriod(value)); break;
            case "totalScore": d.setTotalScore(value); break;
            case "totalRating": d.setTotalRating(value); break;
            case "remark": d.setRemark(value); break;
            case "status": d.setStatus(value); break;
            default:
                if (field != null && field.startsWith("metric")) {
                    try {
                        int idx = Integer.parseInt(field.substring(6));
                        if (idx < 1 || idx > 100) return;
                        // 通过反射设置 metric001-100
                        d.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1), String.class)
                                .invoke(d, value);
                    } catch (Exception ignored) {}
                }
        }
    }

    private String parsePeriod(String value) {
        if (StringUtils.isEmpty(value)) return value;
        // 规则：年度，必须为4位数字
        if (!value.matches("\\d{4}")) {
            throw new ServiceException("评定周期需为年度（YYYY）：" + value);
        }
        return value;
    }

    private String parseDateString(String value, String pattern) {
        if (StringUtils.isEmpty(value)) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            sdf.parse(value);
            return value;
        } catch (ParseException e) {
            throw new ServiceException("日期格式错误：" + value + "，期望 " + pattern);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue();
        return v != null ? v.trim() : null;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        for (int c = 0; c < row.getLastCellNum(); c++) {
            if (StringUtils.isNotEmpty(getCellValue(row.getCell(c)))) return false;
        }
        return true;
    }

    private void validateAssessmentData(SmsStudentAssessment d) {
        if (StringUtils.isEmpty(d.getPersonId())) {
            throw new ServiceException("人员编号不能为空");
        }
        if (StringUtils.isEmpty(d.getPersonName())) {
            throw new ServiceException("姓名不能为空");
        }
        if (StringUtils.isEmpty(d.getUnitId())) {
            throw new ServiceException("单位编号不能为空");
        }
        if (!isValidUnitId(d.getUnitId())) {
            throw new ServiceException("单位编号不符合编码规则（00/01开头）：" + d.getUnitId());
        }
        if (StringUtils.isEmpty(d.getPeriod())) {
            throw new ServiceException("评定周期（年度）不能为空");
        }
    }

    private boolean isValidUnitId(String unitId) {
        return unitId != null && unitId.matches("(00|01)\\d+");
    }
}