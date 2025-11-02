/**
 * 表头配置管理工具类
 * 用于动态加载和管理表格表头配置
 */

// 导入默认配置
import defaultTeacherConfig from '@/config/tableHeaders/default-teacher.json'
import defaultLeaderConfig from '@/config/tableHeaders/default-leader.json'
import defaultStudentConfig from '@/config/tableHeaders/default-student.json'
import defaultDeptConfig from '@/config/tableHeaders/default-dept.json'

/**
 * 表头配置管理器
 */
class TableHeaderConfigManager {
  constructor() {
    this.configCache = new Map()
    this.defaultConfigs = {
      teacher: defaultTeacherConfig,
      leader: defaultLeaderConfig,
      student: defaultStudentConfig,
      dept: defaultDeptConfig
    }
  }

  /**
   * 获取表头配置
   * @param {string} boardType - 看板类型 (teacher/leader)
   * @param {string} year - 年份
   * @param {string} orgCode - 机构编码
   * @returns {Object} 表头配置对象
   */
  async getTableHeaderConfig(boardType, year, orgCode) {
    try {
      // 生成配置键
      const configKey = this.generateConfigKey(boardType, year, orgCode)

      // 检查缓存
      if (this.configCache.has(configKey)) {
        return this.configCache.get(configKey)
      }

      // 尝试加载自定义配置
      const customConfig = await this.loadCustomConfig(boardType, year, orgCode)

      if (customConfig) {
        // 验证配置格式
        const validatedConfig = this.validateConfig(customConfig)
        this.configCache.set(configKey, validatedConfig)
        return validatedConfig
      }

      // 使用默认配置
      const defaultConfig = this.getDefaultConfig(boardType)
      this.configCache.set(configKey, defaultConfig)
      return defaultConfig

    } catch (error) {
      console.warn(`获取表头配置失败，使用默认配置: ${error.message}`)
      return this.getDefaultConfig(boardType)
    }
  }

  /**
   * 加载自定义配置
   * @param {string} boardType - 看板类型
   * @param {string} year - 年份
   * @param {string} orgCode - 机构编码
   * @returns {Object|null} 自定义配置或null
   */
  async loadCustomConfig(boardType, year, orgCode) {
    try {
      // 如果orgCode为空，直接返回null使用默认配置
      if (!orgCode) {
        return null
      }

      // 构建配置文件名
      const configFileName = `${boardType}-${year}-${orgCode}.json`

      // 动态导入配置文件
      const configModule = await import(`@/config/tableHeaders/${configFileName}`)
      return configModule.default || configModule

    } catch (error) {
      // 配置文件不存在或加载失败
      console.log(`自定义配置文件不存在: ${boardType}-${year}-${orgCode}.json`)
      return null
    }
  }

  /**
   * 获取默认配置
   * @param {string} boardType - 看板类型
   * @returns {Object} 默认配置
   */
  getDefaultConfig(boardType) {
    const config = this.defaultConfigs[boardType]
    if (!config) {
      throw new Error(`不支持的看板类型: ${boardType}`)
    }
    return JSON.parse(JSON.stringify(config)) // 深拷贝
  }

  /**
   * 验证配置格式
   * @param {Object} config - 配置对象
   * @returns {Object} 验证后的配置
   */
  validateConfig(config) {
    if (!config || typeof config !== 'object') {
      throw new Error('配置格式无效')
    }

    if (!config.columns || !Array.isArray(config.columns)) {
      throw new Error('配置中缺少columns字段或格式错误')
    }

    // 验证每个列配置
    this.validateColumns(config.columns)

    return config
  }

  /**
   * 验证列配置
   * @param {Array} columns - 列配置数组
   */
  validateColumns(columns) {
    columns.forEach((column, index) => {
      if (!column.label) {
        throw new Error(`第${index + 1}列缺少label字段`)
      }

      // 如果有子列，递归验证
      if (column.children && Array.isArray(column.children)) {
        this.validateColumns(column.children)
      } else if (!column.prop && !column.children) {
        throw new Error(`第${index + 1}列既没有prop字段也没有children字段`)
      }
    })
  }

  /**
   * 生成配置键
   * @param {string} boardType - 看板类型
   * @param {string} year - 年份
   * @param {string} orgCode - 机构编码
   * @returns {string} 配置键
   */
  generateConfigKey(boardType, year, orgCode) {
    return `${boardType}_${year}_${orgCode}`
  }

  /**
   * 清除缓存
   * @param {string} boardType - 看板类型（可选）
   * @param {string} year - 年份（可选）
   * @param {string} orgCode - 机构编码（可选）
   */
  clearCache(boardType, year, orgCode) {
    if (boardType && year && orgCode) {
      // 清除特定配置的缓存
      const configKey = this.generateConfigKey(boardType, year, orgCode)
      this.configCache.delete(configKey)
    } else {
      // 清除所有缓存
      this.configCache.clear()
    }
  }

  /**
   * 获取所有可用的配置文件列表
   * @returns {Array} 配置文件列表
   */
  getAvailableConfigs() {
    // 这里可以扩展为从服务器获取配置文件列表
    return [
      { boardType: 'teacher', year: '2024', orgCode: '0001', description: '2024年机构0001教师成绩配置' },
      { boardType: 'leader', year: '2024', orgCode: '0001', description: '2024年机构0001个人成绩配置' },
      { boardType: 'student', year: '2024', orgCode: '0001', description: '2024年机构0001学生成绩配置' },
      { boardType: 'dept', year: '2024', orgCode: '0001', description: '2024年机构0001单位看板配置' }
      // 可以添加更多配置
    ]
  }
}

// 创建单例实例
const tableHeaderConfigManager = new TableHeaderConfigManager()

export default tableHeaderConfigManager

/**
 * 便捷方法：获取表头配置
 * @param {string} boardType - 看板类型
 * @param {string} year - 年份
 * @param {string} orgCode - 机构编码
 * @returns {Promise<Object>} 表头配置
 */
export const getTableHeaderConfig = (boardType, year, orgCode) => {
  return tableHeaderConfigManager.getTableHeaderConfig(boardType, year, orgCode)
}

/**
 * 便捷方法：清除配置缓存
 * @param {string} boardType - 看板类型（可选）
 * @param {string} year - 年份（可选）
 * @param {string} orgCode - 机构编码（可选）
 */
export const clearTableHeaderCache = (boardType, year, orgCode) => {
  tableHeaderConfigManager.clearCache(boardType, year, orgCode)
}
