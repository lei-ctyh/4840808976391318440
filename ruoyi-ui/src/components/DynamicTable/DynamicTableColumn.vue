<template>
  <el-table-column
    :key="column.key || column.prop || column.label"
    :label="column.label"
    :prop="column.prop"
    :width="column.width"
    :min-width="column.minWidth"
    :fixed="column.fixed"
    :sortable="column.sortable"
    :sort-method="column.sortMethod"
    :sort-by="column.sortBy"
    :sort-orders="column.sortOrders"
    :resizable="column.resizable !== false"
    :formatter="column.formatter"
    :show-overflow-tooltip="column.showOverflowTooltip !== false"
    :align="column.align || 'center'"
    :header-align="column.headerAlign || 'center'"
    :class-name="column.className"
    :label-class-name="column.labelClassName"
    :selectable="column.selectable"
    :reserve-selection="column.reserveSelection"
    :filters="column.filters"
    :filter-placement="column.filterPlacement"
    :filter-multiple="column.filterMultiple"
    :filter-method="column.filterMethod"
    :filtered-value="column.filteredValue"
    :type="column.type"
    :index="column.index"
    :render-header="column.renderHeader"
  >
    <!-- 如果有子列，递归渲染 -->
    <template v-if="column.children && column.children.length > 0">
      <dynamic-table-column
        v-for="childColumn in column.children"
        :key="childColumn.key || childColumn.prop || childColumn.label"
        :column="childColumn"
      />
    </template>

    <!-- 特殊类型列（selection/index）不提供默认插槽，保持Element原生渲染 -->
    <template v-else-if="column.type === 'selection'"></template>
    <template v-else-if="column.type === 'index'"></template>

    <!-- 其他情况统一使用一个默认插槽，在内部进行条件渲染 -->
    <template v-else slot-scope="scope">
      <!-- 自定义插槽内容 -->
      <template v-if="column.slot">
        <slot
          :name="column.slot"
          :row="scope.row"
          :column="scope.column"
          :$index="scope.$index"
          :value="scope.row[column.prop]"
        >
          {{ scope.row[column.prop] }}
        </slot>
      </template>

      <!-- 自定义渲染函数 -->
      <template v-else-if="column.render">
        <render-cell
          :render="column.render"
          :row="scope.row"
          :column="scope.column"
          :index="scope.$index"
        />
      </template>

      <!-- 展开列 -->
      <template v-else-if="column.type === 'expand'">
        <slot name="expand" :row="scope.row" :$index="scope.$index">
          <div>展开内容</div>
        </slot>
      </template>

      <!-- 默认内容：显示字典标签或原始值 -->
      <template v-else>
        <dict-tag
          v-if="column.dictType"
          :options="getDictOptions(column.dictType)"
          :value="scope.row[column.prop]"
        />
        <span v-else-if="column.dateFormat">
          {{ formatDate(scope.row[column.prop], column.dateFormat) }}
        </span>
        <span v-else-if="column.numberFormat">
          {{ formatNumber(scope.row[column.prop], column.numberFormat) }}
        </span>
        <span v-else>
          {{ scope.row[column.prop] }}
        </span>
      </template>
    </template>
  </el-table-column>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'

// 渲染单元格组件
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
      required: true
    }
  },
  methods: {
    /**
     * 获取字典选项
     * @param {string} dictType - 字典类型
     * @returns {Array} 字典选项数组
     */
    getDictOptions(dictType) {
      // 从Vuex store中获取字典数据
      if (this.$store && this.$store.getters && this.$store.getters.dict) {
        const dictData = this.$store.getters.dict[dictType]
        return dictData || []
      }
      return []
    },

    /**
     * 格式化日期
     * @param {string|Date} date - 日期值
     * @param {string} format - 格式化模式
     * @returns {string} 格式化后的日期字符串
     */
    formatDate(date, format = '{y}-{m}-{d} {h}:{i}:{s}') {
      if (!date) return ''
      return parseTime(date, format)
    },

    /**
     * 格式化数字
     * @param {number} number - 数字值
     * @param {Object} options - 格式化选项
     * @returns {string} 格式化后的数字字符串
     */
    formatNumber(number, options = {}) {
      if (number === null || number === undefined || number === '') return ''
      
      const {
        decimals = 2,           // 小数位数
        thousandsSeparator = ',', // 千分位分隔符
        decimalSeparator = '.',   // 小数点分隔符
        prefix = '',              // 前缀
        suffix = ''               // 后缀
      } = options

      // 转换为数字
      const num = parseFloat(number)
      if (isNaN(num)) return number

      // 格式化数字
      const parts = num.toFixed(decimals).split('.')
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator)
      
      let result = parts.join(decimalSeparator)
      
      // 添加前缀和后缀
      if (prefix) result = prefix + result
      if (suffix) result = result + suffix
      
      return result
    }
  }
}
</script>

<style scoped>
/* 这里可以添加特定于列的样式 */
</style>