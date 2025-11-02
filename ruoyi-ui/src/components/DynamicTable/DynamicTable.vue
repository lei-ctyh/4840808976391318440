<template>
  <div class="dynamic-table-container">
    <!-- 表格加载状态 -->
    <div v-if="configLoading" class="config-loading">
      <i class="el-icon-loading"></i>
      <span>正在加载表格配置...</span>
    </div>

    <!-- 配置加载失败提示 -->
    <div v-else-if="configError" class="config-error">
      <el-alert
        :title="configError"
        type="error"
        :closable="false"
        show-icon>
      </el-alert>
    </div>

    <!-- 动态表格 -->
    <el-table
      v-else
      :data="tableData"
      :cell-class-name="getCellClassName"
      v-bind="tableProps"
      v-on="tableListeners"
      ref="dynamicTable"
    >
      <!-- 渲染表格列（避免使用 <template v-for> 包裹） -->
      <dynamic-table-column
        v-for="column in tableColumns"
        :key="column.key || column.prop || column.label"
        :column="column"
      />
    </el-table>
  </div>
</template>

<script>
import { getTableHeaderConfig } from '@/utils/tableHeaderConfig'
import DynamicTableColumn from './DynamicTableColumn.vue'

export default {
  name: 'DynamicTable',
  components: {
    DynamicTableColumn
  },
  props: {
    // 表格数据
    data: {
      type: Array,
      default: () => []
    },
    // 看板类型
    boardType: {
      type: String,
      required: true,
      validator: value => ['teacher', 'leader', 'student', 'dept'].includes(value)
    },
    // 年份
    year: {
      type: String,
      required: true
    },
    // 机构编码
    orgCode: {
      type: String,
      default: ''
    },
    // 表格属性
    tableProps: {
      type: Object,
      default: () => ({
        border: true,
        size: 'small',
        style: 'width: 100%',
        'header-cell-style': { textAlign: 'center' }
      })
    },
    // 是否自动加载配置
    autoLoad: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      tableColumns: [],
      configLoading: false,
      configError: null,
      currentConfig: null
    }
  },
  computed: {
    tableData() {
      console.log(this.data)
      return this.data || []
    },
    tableListeners() {
      // 传递所有父组件的事件监听器
      return this.$listeners
    }
  },
  watch: {
    // 监听关键参数变化，重新加载配置
    boardType: {
      handler: 'loadTableConfig',
      immediate: false
    },
    year: {
      handler: 'loadTableConfig',
      immediate: false
    },
    orgCode: {
      handler: 'loadTableConfig',
      immediate: false
    }
  },
  async mounted() {
    if (this.autoLoad) {
      await this.loadTableConfig()
    }
  },
  methods: {
    /**
     * 加载表格配置
     */
    async loadTableConfig() {
      if (!this.boardType || !this.year) {
        this.configError = '缺少必要参数：boardType 或 year'
        return
      }

      this.configLoading = true
      this.configError = null

      try {
        // 获取表头配置，orgCode可以为空
        const config = await getTableHeaderConfig(this.boardType, this.year, this.orgCode || '')

        if (!config || !config.columns) {
          throw new Error('配置格式错误：缺少columns字段')
        }

        // 处理配置并生成表格列
        this.currentConfig = config
        this.tableColumns = this.processColumns(config.columns)
        console.log('加载表格配置成功:', this.tableColumns)

        // 触发配置加载完成事件
        this.$emit('config-loaded', config)

      } catch (error) {
        console.error('加载表格配置失败:', error)
        this.configError = `加载表格配置失败: ${error.message}`
        this.$emit('config-error', error)
      } finally {
        this.configLoading = false
      }
    },

    /**
     * 处理列配置，添加必要的属性
     * @param {Array} columns - 原始列配置
     * @param {string} parentKey - 父级键值
     * @returns {Array} 处理后的列配置
     */
    processColumns(columns, parentKey = '') {
      return columns.map((column, index) => {
        // 生成唯一键值
        const key = parentKey ? `${parentKey}-${index}` : `col-${index}`

        // 处理后的列配置
        const processedColumn = {
          ...column,
          key
        }

        // 如果有子列，递归处理
        if (column.children && Array.isArray(column.children)) {
          processedColumn.children = this.processColumns(column.children, key)
        }

        return processedColumn
      })
    },

    /**
     * 手动重新加载配置
     */
    async reloadConfig() {
      await this.loadTableConfig()
    },

    /**
     * 获取当前配置
     */
    getCurrentConfig() {
      return this.currentConfig
    },

    /**
     * 获取表格实例
     */
    getTableRef() {
      return this.$refs.dynamicTable
    },

    /**
     * 清除选择
     */
    clearSelection() {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.clearSelection) {
        tableRef.clearSelection()
      }
    },

    /**
     * 切换行选择状态
     */
    toggleRowSelection(row, selected) {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.toggleRowSelection) {
        tableRef.toggleRowSelection(row, selected)
      }
    },

    /**
     * 设置行展开状态
     */
    toggleRowExpansion(row, expanded) {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.toggleRowExpansion) {
        tableRef.toggleRowExpansion(row, expanded)
      }
    },

    /**
     * 手动排序
     */
    sort(prop, order) {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.sort) {
        tableRef.sort(prop, order)
      }
    },

    /**
     * 清除排序
     */
    clearSort() {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.clearSort) {
        tableRef.clearSort()
      }
    },

    /**
     * 清除筛选
     */
    clearFilter(columnKeys) {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.clearFilter) {
        tableRef.clearFilter(columnKeys)
      }
    },

    /**
     * 重新布局表格
     */
    doLayout() {
      const tableRef = this.getTableRef()
      if (tableRef && tableRef.doLayout) {
        tableRef.doLayout()
      }
    },

    /**
     * 获取行的CSS类名，用于高亮包含"不及格"的行
     * @param {Object} row - 行数据
     * @param {number} rowIndex - 行索引
     * @returns {string} CSS类名
     */
    getRowClassName({ row, rowIndex }) {
      // 检查行数据中是否包含"不及格"字样
      const rowValues = Object.values(row).join(' ')
      if (rowValues.includes('未及格')) {
        return 'failing-row'
      }
      return ''
    }
    ,
    /**
     * 获取单元格的CSS类名，仅高亮包含“未及格/不及格”的单元格
     * @param {Object} row - 行数据
     * @param {Object} column - 列对象（含property/prop）
     * @param {number} rowIndex - 行索引
     * @param {number} columnIndex - 列索引
     * @returns {string} CSS类名
     */
    getCellClassName({ row, column, rowIndex, columnIndex }) {
      const prop = column?.property || column?.prop
      const value = prop ? row[prop] : ''
      const text = value == null ? '' : String(value)
      if (text.includes('未及格') || text.includes('不及格')) {
        return 'failing-cell'
      }
      return ''
    }
  }
}
</script>

<style scoped>
.dynamic-table-container {
  width: 100%;
}

.config-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #606266;
}

.config-loading i {
  margin-right: 8px;
  font-size: 16px;
}

.config-error {
  margin-bottom: 16px;
}

/* 表格样式优化 */
.dynamic-table-container :deep(.el-table) {
  font-size: 12px;
}

.dynamic-table-container :deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.dynamic-table-container :deep(.el-table td) {
  padding: 8px 0;
}

/* 嵌套表头样式 */
.dynamic-table-container :deep(.el-table .el-table__header th) {
  text-align: center;
  border-right: 1px solid #ebeef5;
}

.dynamic-table-container :deep(.el-table .el-table__body td) {
  text-align: center;
  border-right: 1px solid #ebeef5;
}

/* 固定列样式 */
.dynamic-table-container :deep(.el-table .el-table__fixed-column--left) {
  background-color: #fafafa;
}

.dynamic-table-container :deep(.el-table .el-table__fixed-column--right) {
  background-color: #fafafa;
}

/* 不及格单元格高亮样式 */
::v-deep .el-table__body td.failing-cell {
  background-color: red;
}
</style>
