# 动态多层级表头表格组件

## 概述

动态表格组件支持根据配置文件动态生成多层级表头，适用于干部成绩和个人成绩等需要灵活表头配置的场景。

## 组件结构

```
DynamicTable/
├── index.js              # 组件入口文件
├── DynamicTable.vue      # 主表格组件
├── DynamicTableColumn.vue # 递归列组件
└── README.md            # 使用说明
```

## 配置文件结构

### 配置文件位置
- 默认配置：`src/config/tableHeaders/default-{boardType}.json`
- 自定义配置：`src/config/tableHeaders/{boardType}-{year}-{orgCode}.json`

### 配置文件格式

```json
{
  "version": "1.0",
  "boardType": "teacher",
  "description": "干部成绩表头配置",
  "columns": [
    {
      "prop": "personId",
      "label": "人员编号",
      "width": 110,
      "type": "text"
    },
    {
      "label": "基础科目 20%",
      "children": [
        {
          "prop": "baseBasicKnowledge",
          "label": "基本知识 20%",
          "width": 140,
          "type": "number"
        },
        {
          "label": "体育 30%",
          "children": [
            {
              "prop": "baseSportsTrack",
              "label": "田径",
              "width": 100,
              "type": "number"
            }
          ]
        }
      ]
    }
  ]
}
```

### 列配置属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| prop | String | 否 | 数据字段名，叶子节点必填 |
| label | String | 是 | 列标题 |
| width | Number | 否 | 列宽度 |
| minWidth | Number | 否 | 最小列宽 |
| type | String | 否 | 列类型：text/number/date/dict/selection/index/expand |
| children | Array | 否 | 子列配置，用于多层级表头 |
| fixed | String | 否 | 固定列：left/right |
| sortable | Boolean | 否 | 是否可排序 |
| align | String | 否 | 对齐方式：left/center/right |
| headerAlign | String | 否 | 表头对齐方式 |
| showOverflowTooltip | Boolean | 否 | 内容过长时显示tooltip |
| formatter | Function | 否 | 自定义格式化函数 |
| render | Function | 否 | 自定义渲染函数 |
| dictType | String | 否 | 字典类型（type为dict时使用） |
| dateFormat | String | 否 | 日期格式（type为date时使用） |

## 使用方法

### 1. 基本使用

```vue
<template>
  <dynamic-table
    :data="tableData"
    board-type="teacher"
    :year="selectedYear"
    :org-code="currentOrgCode"
    :table-props="{
      border: true,
      size: 'small',
      style: 'width: 100%'
    }"
    @config-loaded="onConfigLoaded"
    @config-error="onConfigError"
    ref="dynamicTable"
  />
</template>

<script>
import DynamicTable from "@/components/DynamicTable"

export default {
  components: { DynamicTable },
  data() {
    return {
      tableData: [],
      selectedYear: "2024",
      currentOrgCode: "0001"
    }
  },
  methods: {
    onConfigLoaded(config) {
      console.log('配置加载成功:', config)
    },
    onConfigError(error) {
      console.error('配置加载失败:', error)
      this.$message.warning('表格配置加载失败，已使用默认配置')
    }
  }
}
</script>
```

### 2. 组件属性

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| data | Array | 是 | [] | 表格数据 |
| boardType | String | 是 | - | 看板类型：teacher/leader |
| year | String | 是 | - | 年份 |
| orgCode | String | 否 | null | 机构编码 |
| tableProps | Object | 否 | {} | el-table的属性 |

### 3. 组件事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| config-loaded | config | 配置加载成功时触发 |
| config-error | error | 配置加载失败时触发 |

### 4. 组件方法

通过ref可以调用以下方法：

```javascript
// 获取当前配置
const config = this.$refs.dynamicTable.getCurrentConfig()

// 重新加载配置
this.$refs.dynamicTable.reloadConfig()

// 获取el-table实例
const table = this.$refs.dynamicTable.getTableRef()
```

## 配置加载规则

1. 根据 `boardType`、`year`、`orgCode` 生成配置文件路径
2. 优先加载自定义配置：`{boardType}-{year}-{orgCode}.json`
3. 如果自定义配置不存在，加载默认配置：`default-{boardType}.json`
4. 配置加载失败时触发 `config-error` 事件

## 机构编码规则

根据用户规则，机构编码遵循以下规范：

- 00开头：领导班子
- 01开头：教学组织
- 00XX：领导班子下的部门
- 00XXXX：领导班子下的科
- 00XXXXXX：教学组织下的室
- 01XX：教学组织下的系
- 01XXXX：教学组织下的班
- 01XXXXXX：教学组织下的组

## 示例配置文件

### 干部成绩默认配置 (default-teacher.json)

```json
{
  "version": "1.0",
  "boardType": "teacher",
  "description": "干部成绩默认表头配置",
  "columns": [
    {
      "prop": "personId",
      "label": "人员编号",
      "width": 110,
      "type": "text"
    },
    {
      "prop": "name",
      "label": "姓名",
      "width": 100,
      "type": "text"
    },
    {
      "label": "基础科目 20%",
      "children": [
        {
          "prop": "baseBasicKnowledge",
          "label": "基本知识 20%",
          "width": 140,
          "type": "number"
        },
        {
          "label": "体育 30%",
          "children": [
            {
              "prop": "baseSportsTrack",
              "label": "田径",
              "width": 100,
              "type": "number"
            },
            {
              "prop": "baseSportsRope",
              "label": "跳绳",
              "width": 100,
              "type": "number"
            },
            {
              "prop": "baseSportsLongJump",
              "label": "跳远",
              "width": 100,
              "type": "number"
            }
          ]
        }
      ]
    }
  ]
}
```

### 自定义配置示例 (teacher-2024-0001.json)

```json
{
  "version": "1.0",
  "boardType": "teacher",
  "year": "2024",
  "orgCode": "0001",
  "description": "2024年机构0001干部成绩自定义配置",
  "columns": [
    {
      "prop": "personId",
      "label": "人员编号",
      "width": 110,
      "type": "text"
    },
    {
      "label": "理论考核 30%",
      "children": [
        {
          "prop": "theoryProfessional",
          "label": "专业理论",
          "width": 120,
          "type": "number"
        },
        {
          "prop": "theoryPolitical",
          "label": "政治理论",
          "width": 120,
          "type": "number"
        }
      ]
    }
  ]
}
```

## 注意事项

1. 配置文件必须是有效的JSON格式
2. 叶子节点必须包含 `prop` 属性
3. 配置文件路径区分大小写
4. 建议为每个配置文件添加版本号和描述信息
5. 自定义配置会完全覆盖默认配置，不会合并