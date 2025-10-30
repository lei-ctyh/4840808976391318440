import DynamicTable from './DynamicTable.vue'
import DynamicTableColumn from './DynamicTableColumn.vue'

// 导出组件
export { DynamicTable, DynamicTableColumn }

// 默认导出主组件
export default DynamicTable

// 安装函数，用于全局注册
export const install = function(Vue) {
  Vue.component('DynamicTable', DynamicTable)
  Vue.component('DynamicTableColumn', DynamicTableColumn)
}

// 如果在浏览器环境中直接引入，自动安装
if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}