# 表头配置 JSON 使用说明

本文档说明 `ruoyi-ui/src/config/tableHeaders` 目录下表头配置 JSON 的用途、命名规范与字段含义，便于扩展学生、教师、个人成绩的列展示与分组表头。

## 文件作用
- 定义看板（学生/教师/领导）表格的列顺序、显示名称、宽度与是否固定列。
- 支持多级分组表头，通过 `children` 嵌套实现二级甚至多级列组。
- 与后端返回数据的字段进行映射，前端无需改动组件即可切换不同表头方案。

## 命名规范
- 默认模板：`default-student.json`、`default-teacher.json`、`default-leader.json`。
- 自定义模板建议：`<boardType>-<year>-<code>.json`，例如：`teacher-2024-0001.json`。
  - `boardType` 取值：`student`、`teacher`、`leader`。
  - `year` 为年度（评定周期），建议与数据一致，如 `2024`。
  - `code` 为自定义编号或版本号，区分不同方案。

## 顶层结构
```json
{
  "boardType": "student",
  "description": "学生成绩默认表头配置",
  "version": "1.0.0",
  "columns": [ /* 列配置数组，见下文 */ ]
}
```
- `boardType`：看板类型，`student`/`teacher`/`leader`。
- `description`：配置说明，便于识别用途。
- `version`：版本号，语义化字符串。
- `columns`：列配置数组，支持叶子列与分组列两种形态。

## 列配置（叶子列）
```json
{
  "label": "人员编号",
  "prop": "personId",
  "width": "110",
  "fixed": true
}
```
- `label`：列头显示名称。
- `prop`：数据字段名，需与表格数据源中的属性一致。
- `width`：列固定宽度（字符串或数字）。内容较多可使用 `minWidth`。
- `minWidth`：列最小宽度，适合变长文本（与 `width` 二选一）。
- `fixed`：是否固定列（布尔值）。`true` 表示固定在左侧。

## 列配置（分组列）
```json
{
  "label": "共同科目30%",
  "children": [
    { "label": "课目1", "prop": "commonSubject1", "width": "100" },
    { "label": "课目2", "prop": "commonSubject2", "width": "100" }
  ]
}
```
- 仅当存在 `children` 时视为分组列；组内为子列数组。
- 分组列通常不需要 `prop`；如需“组汇总”列，也可同时设置 `prop` 与 `width`（见 `岗位业务 50%` 的示例）。

## 与后端字段的映射
- 常见通用字段：`personId`、`personName`、`unitPath`（前端拼接单位路径）、`birthDate`、`age`、`title`（学生显示为“班级”）、`period`、`remark`、`description` 等。
- 学生指标字段：`metric001` ~ `metric100`（后端为字符串类型）。如需展示，列 `label` 可写为“指标001”等，`prop` 对应上述字段名。
- 仅当数据源包含某 `prop` 时，该列才有值；新增列前请确保接口或前端组装提供该字段。

## 设计建议（结合项目规则）
- 考核周期为年度：建议保留 `period` 列，并默认展示当前年度数据。
- 机构编码规则：单位展示使用 `unitPath`（路径/名称），避免直接暴露编码细节；若需编码，可加列 `unitId`。
- 固定列：将 `personId`、`personName` 设为固定列，提升可读性。
- 宽度选择：短字段用 `width`，长文本（备注/说明/单位路径）用 `minWidth`。
- 分组深度：建议不超过两级，保持表头清晰。

## 示例（学生）
```json
{
  "boardType": "student",
  "description": "学生成绩自定义方案",
  "version": "1.1.0",
  "columns": [
    { "label": "人员编号", "prop": "personId", "width": "110", "fixed": true },
    { "label": "姓名", "prop": "personName", "width": "100", "fixed": true },
    { "label": "单位", "prop": "unitPath", "minWidth": "220" },
    { "label": "出生年月", "prop": "birthDate", "width": "120" },
    { "label": "年龄", "prop": "age", "width": "80" },
    { "label": "班级", "prop": "title", "width": "100" },
    { "label": "评定周期", "prop": "period", "width": "120" },
    {
      "label": "指标（示例）",
      "children": [
        { "label": "指标001", "prop": "metric001", "width": "90" },
        { "label": "指标002", "prop": "metric002", "width": "90" }
      ]
    }
  ]
}
```

## 常见问题
- 列不显示：确认 `prop` 是否存在于数据源；检查大小写与拼写。
- 表头错位：分组层级不一致或缺少 `children` 导致；保持同级列结构一致。
- 列过宽/过窄：使用合适的 `width`/`minWidth`；长文本建议使用 `minWidth`。
- 固定列冲突：过多固定列会影响滚动体验，建议只固定关键标识列。

## 变更说明
- 学生领域模型使用 `title` 字段作为“班级”显示；对应导入导出模板也以“班级”呈现。