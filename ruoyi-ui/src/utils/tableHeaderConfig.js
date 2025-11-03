/**
 * 表头配置管理工具类
 * 用于动态加载和管理表格表头配置
 */

/**
 * 表头配置管理器
 */
class TableHeaderConfigManager {
  constructor() {
    this.configCache = new Map()
    // 配置文件的基础路径
    this.configBasePath = '/config/tableHeaders'
  }

  /**
   * 获取表头配置
   * @param {string} boardType - 看板类型 (teacher/leader/student/dept)
   * @param {string} year - 年份
   * @param {Array} orgCodePath - 机构路径数组（从当前到根）['0101', '01', '']
   * @returns {Object} 表头配置对象
   */
  async getTableHeaderConfig(boardType, year, orgCodePath = []) {
    try {
      // 确保 orgCodePath 是数组
      const orgCodes = Array.isArray(orgCodePath) ? orgCodePath : [orgCodePath]

      // 不使用缓存，每次都重新加载配置文件（实时读取）
      console.log(`[配置加载] 开始查找配置: boardType=${boardType}, year=${year}, orgCodes=${JSON.stringify(orgCodes)}`)

      // 尝试多级降级加载配置
      const config = await this.tryLoadConfigWithFallback(boardType, year, orgCodes)

      if (config) {
        // 验证配置格式
        const validatedConfig = this.validateConfig(config)
        return validatedConfig
      }

      // 使用默认配置
      console.log(`[配置加载] 使用默认配置: default-${boardType}.json`)
      const defaultConfig = await this.getDefaultConfig(boardType)
      return defaultConfig

    } catch (error) {
      console.warn(`获取表头配置失败，使用默认配置: ${error.message}`)
      return await this.getDefaultConfig(boardType)
    }
  }

  /**
   * 多级降级加载配置
   * 尝试顺序：类型+年份+机构 → 类型+机构 → 父级机构（重复） → 默认配置
   * @param {string} boardType - 看板类型
   * @param {string} year - 年份
   * @param {Array} orgCodePath - 机构路径数组（从当前到根）
   * @returns {Object|null} 配置对象或null
   */
  async tryLoadConfigWithFallback(boardType, year, orgCodePath) {
    // 过滤掉空的 orgCode
    const validOrgCodes = orgCodePath.filter(code => code && code.trim())

    // 如果没有有效的机构编码，直接返回null使用默认配置
    if (validOrgCodes.length === 0) {
      console.log('没有有效的机构编码，将使用默认配置')
      return null
    }

    // 遍历每一级机构（从当前到根）
    for (const orgCode of validOrgCodes) {
      // 1. 尝试：类型+年份+机构 (teacher-2025-0101.json)
      const configWithYear = await this.tryLoadConfig(boardType, year, orgCode)
      if (configWithYear) {
        console.log(`✓ 找到配置: ${boardType}-${year}-${orgCode}.json`)
        return configWithYear
      }

      // 2. 尝试：类型+机构 (teacher-0101.json)
      const configWithoutYear = await this.tryLoadConfig(boardType, null, orgCode)
      if (configWithoutYear) {
        console.log(`✓ 找到配置: ${boardType}-${orgCode}.json`)
        return configWithoutYear
      }

      console.log(`  未找到机构 ${orgCode} 的配置，尝试上级机构...`)
    }

    // 所有机构层级都没找到配置
    console.log('所有层级都未找到自定义配置，将使用默认配置')
    return null
  }

  /**
   * 尝试加载单个配置文件
   * @param {string} boardType - 看板类型
   * @param {string} year - 年份（可为null）
   * @param {string} orgCode - 机构编码
   * @returns {Object|null} 配置对象或null
   */
  async tryLoadConfig(boardType, year, orgCode) {
    try {
      // 构建配置文件名
      const configFileName = year
        ? `${boardType}-${year}-${orgCode}.json`
        : `${boardType}-${orgCode}.json`

      // 添加时间戳参数禁用浏览器缓存
      const timestamp = new Date().getTime()
      const configUrl = `${this.configBasePath}/${configFileName}?t=${timestamp}`

      // 使用 fetch 加载配置文件，禁用缓存
      const response = await fetch(configUrl, {
        cache: 'no-store', // 禁用HTTP缓存
        headers: {
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      })

      if (response.ok) {
        return await response.json()
      }

      return null

    } catch (error) {
      return null
    }
  }

  /**
   * 获取默认配置
   * @param {string} boardType - 看板类型
   * @returns {Promise<Object>} 默认配置
   */
  async getDefaultConfig(boardType) {
    try {
      const configFileName = `default-${boardType}.json`

      // 添加时间戳参数禁用浏览器缓存
      const timestamp = new Date().getTime()
      const configUrl = `${this.configBasePath}/${configFileName}?t=${timestamp}`

      const response = await fetch(configUrl, {
        cache: 'no-store', // 禁用HTTP缓存
        headers: {
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      })

      if (!response.ok) {
        throw new Error(`不支持的看板类型或配置文件不存在: ${boardType}`)
      }

      return await response.json()

    } catch (error) {
      throw new Error(`加载默认配置失败: ${error.message}`)
    }
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
      { boardType: 'teacher', year: '2024', orgCode: '0001', description: '2024年机构0001干部成绩配置' },
      { boardType: 'leader', year: '2024', orgCode: '0001', description: '2024年机构0001个人成绩配置' },
      { boardType: 'student', year: '2024', orgCode: '0001', description: '2024年机构0001战士成绩配置' },
      { boardType: 'dept', year: '2024', orgCode: '0001', description: '2024年机构0001单位成绩配置' }
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
 * @param {Array|string} orgCodePath - 机构路径数组（从当前到根）或单个机构编码
 * @returns {Promise<Object>} 表头配置
 */
export const getTableHeaderConfig = (boardType, year, orgCodePath) => {
  return tableHeaderConfigManager.getTableHeaderConfig(boardType, year, orgCodePath)
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
