<template>
  <el-table-column
    :key="getColumnKey(column)"
    v-bind="getColumnProps(column)"
  >
    <!-- 递归渲染子列 -->
    <template v-if="hasChildren(column)">
      <dynamic-table-column
        v-for="childColumn in column.children"
        :key="getColumnKey(childColumn)"
        :column="childColumn"
        :style="{ backgroundColor: '#ffebee'}"
      />
    </template>

    <!-- 单元格内容渲染：避免在默认插槽上使用 v-else，改为显式 v-if -->
    <template v-if="!hasChildren(column)" slot-scope="scope">
      <!-- 特殊类型列不渲染默认内容，让 ElementUI 原生处理 -->
      <template v-if="isSpecialType(column)"></template>

      <!-- 自定义插槽：将行、列、索引与当前值传给父级插槽 -->
      <slot
        v-else-if="column.slot"
        :name="column.slot"
        :row="scope.row"
        :column="scope.column"
        :$index="scope.$index"
        :value="scope.row[column.prop]"
      >
        {{ formatCellValue(scope.row[column.prop], column) }}
      </slot>

      <!-- 自定义渲染函数 -->
      <render-cell
        v-else-if="typeof column.render === 'function'"
        :render="column.render"
        :row="scope.row"
        :column="scope.column"
        :index="scope.$index"
      />

      <!-- 展开列内容 -->
      <template v-else-if="column.type === 'expand'">
        <slot name="expand" :row="scope.row" :$index="scope.$index">
          <div>展开内容</div>
        </slot>
      </template>

      <!-- 字典、日期、数字格式化或原始值 -->
      <dict-tag
        v-else-if="column.dictType"
        :options="getDictOptions(column.dictType)"
        :value="scope.row[column.prop]"
      />
      <span v-else>
        {{ formatCellValue(scope.row[column.prop], column) }}
      </span>
    </template>
  </el-table-column>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'

// 渲染单元格组件（用于调用自定义渲染函数）
const RenderCell = {
  functional: true,
  props: {
    render: Function,
    row: Object,
    column: Object,
    index: Number
  },
  render(h, ctx) {
    const { render, row, column, index } = ctx.props
    return render(h, { row, column, index })
  }
}

export default {
  name: 'DynamicTableColumn',
  components: {
    'render-cell': RenderCell
  },
  props: {
    column: {
      type: Object,
      required: true,
      validator(value) {
        return !!(value.key || value.prop || value.label)
      }
    },
    // 添加字典数据注入选项
    dictData: {
      type: Object,
      default: () => ({})
    }
  },
  provide() {
    return {
      dynamicTableColumn: this
    }
  },
  methods: {
    getColumnKey(column) {
      return column.key || column.prop || column.label
    },

    getColumnProps(column) {
      const baseProps = {
        label: column.label,
        prop: column.prop,
        width: column.width,
        minWidth: column.minWidth,
        fixed: column.fixed,
        sortable: column.sortable,
        resizable: column.resizable !== false,
        align: column.align || 'center',
        headerAlign: column.headerAlign || 'center',
        showOverflowTooltip: column.showOverflowTooltip !== false
      }

      // 添加可选属性
      const optionalProps = [
        'sortMethod', 'sortBy', 'sortOrders', 'formatter', 'className',
        'labelClassName', 'selectable', 'reserveSelection', 'filters',
        'filterPlacement', 'filterMultiple', 'filterMethod', 'filteredValue',
        'type', 'index', 'renderHeader'
      ]

      optionalProps.forEach(prop => {
        if (column[prop] !== undefined) {
          baseProps[prop] = column[prop]
        }
      })

      return baseProps
    },

    hasChildren(column) {
      return Array.isArray(column.children) && column.children.length > 0
    },

    isSpecialType(column) {
      return ['selection', 'index'].includes(column.type)
    },

    getDictOptions(dictType) {
      // 优先使用注入的dictData，其次使用store
      if (this.dictData && this.dictData[dictType]) {
        return this.dictData[dictType]
      }

      if (this.$store?.getters?.dict?.[dictType]) {
        return this.$store.getters.dict[dictType]
      }

      return []
    },

    formatCellValue(value, column) {
      if (value == null) return ''

      if (column.dateFormat) {
        return this.formatDate(value, column.dateFormat)
      }

      if (column.numberFormat) {
        return this.formatNumber(value, column.numberFormat)
      }

      return value
    },

    formatDate(date, format) {
      if (!date) return ''
      try {
        return parseTime(date, format)
      } catch (error) {
        console.warn('Date format error:', error)
        return date
      }
    },

    formatNumber(number, options = {}) {
      if (number === null || number === undefined || number === '') return ''

      try {
        const {
          decimals = 2,
          thousandsSeparator = ',',
          decimalSeparator = '.',
          prefix = '',
          suffix = ''
        } = options

        const num = parseFloat(number)
        if (isNaN(num)) return String(number)

        const parts = num.toFixed(decimals).split('.')
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator)

        let result = parts.join(decimalSeparator)
        if (prefix) result = prefix + result
        if (suffix) result = result + suffix

        return result
      } catch (error) {
        console.warn('Number format error:', error)
        return String(number)
      }
    }
  }
}
</script>

<style scoped>
/* 响应式设计 */
.el-table-column {
  transition: all 0.3s ease;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .el-table-column {
    min-width: 100px !important;
  }
}
</style>
